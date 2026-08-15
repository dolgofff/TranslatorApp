package com.example.translatorapp.domain.media.camera

import com.example.translatorapp.domain.model.ml.RecognizedText
import com.example.translatorapp.domain.model.ml.RecognizedTextBlock
import com.example.translatorapp.domain.model.ml.TextBounds

class TextStabilizer {
    private val trackedBlocks = mutableListOf<TrackedBlock>()
    private var nextId = 0L

    private companion object {
        const val REQUIRED_DETECTIONS = 3
        const val MAX_MISSED_FRAMES = 1
        const val BOUNDS_SMOOTHING_ALPHA = 0.25f
        const val MIN_IOU_THRESHOLD = 0.25f
        const val MIN_TEXT_SIMILARITY = 0.60f
    }

    fun stabilize(recognizedText: RecognizedText): RecognizedText {
        val matchedTrackedIds = mutableSetOf<Long>()

        recognizedText.blocks.forEach { currentBlock ->
            val currentText = normalize(currentBlock.text)
            val currentBounds = currentBlock.bounds

            if (currentText.isBlank() || currentBounds == null)
                return@forEach

            val match = findBestMatch(
                currentText = currentText,
                currentBounds = currentBounds,
                alreadyMatchedIds = matchedTrackedIds
            )

            if (match == null) {
                val newTrackedBlock = TrackedBlock(
                    id = nextId++,
                    stableText = currentBlock.text,
                    candidateText = "",
                    candidateDetections = 0,
                    bounds = currentBounds,
                    detections = 1,
                    missedFrames = 0
                )

                trackedBlocks += newTrackedBlock
                matchedTrackedIds += newTrackedBlock.id

                return@forEach
            }

            val textUpdated = updateStableText(tracked = match, currentText = currentBlock.text)

            val updated = textUpdated.copy(
                bounds = smoothBounds(previous = match.bounds, current = currentBounds),
                detections = match.detections + 1,
                missedFrames = 0
            )

            val index = trackedBlocks.indexOfFirst { it.id == match.id }

            if (index >= 0)
                trackedBlocks[index] = updated

            matchedTrackedIds += match.id
        }

        updateMissedFrames(matchedTrackedIds = matchedTrackedIds)

        val stableBlocks = trackedBlocks
            .filter { tracked ->
                tracked.detections >= REQUIRED_DETECTIONS && tracked.missedFrames == 0
            }
            .map { tracked ->
                RecognizedTextBlock(text = tracked.stableText, bounds = tracked.bounds)
            }

        return RecognizedText(blocks = stableBlocks)
    }

    private fun updateStableText(tracked: TrackedBlock, currentText: String): TrackedBlock {
        val normalizedCurrent = normalize(currentText)
        val normalizedStable = normalize(tracked.stableText)

        if (normalizedCurrent == normalizedStable)
            return tracked.copy(candidateText = "", candidateDetections = 0)

        val normalizedCandidate = normalize(tracked.candidateText)
        val sameCandidate = normalizedCurrent == normalizedCandidate

        val newCandidateText = if (sameCandidate) tracked.candidateText else currentText
        val newCandidateDetections = if (sameCandidate) tracked.candidateDetections + 1 else 1

        return if (newCandidateDetections >= 3)
            tracked.copy(
                stableText = newCandidateText,
                candidateText = "",
                candidateDetections = 0
            )
        else
            tracked.copy(
                candidateText = newCandidateText,
                candidateDetections = newCandidateDetections
            )
    }

    private fun findBestMatch(
        currentText: String,
        currentBounds: TextBounds,
        alreadyMatchedIds: Set<Long>,
    ): TrackedBlock? {
        var bestMatch: TrackedBlock? = null
        var bestScore = 0f

        trackedBlocks.forEach { tracked ->
            if (tracked.id in alreadyMatchedIds)
                return@forEach

            val iou = calculateIoU(first = tracked.bounds, second = currentBounds)

            if (iou < MIN_IOU_THRESHOLD)
                return@forEach

            val textSimilarity = calculateTextSimilarity(
                first = normalize(tracked.stableText),
                second = currentText
            )

            if (textSimilarity < MIN_TEXT_SIMILARITY)
                return@forEach

            val score = iou * 0.6f + textSimilarity * 0.4f

            if (score > bestScore) {
                bestScore = score
                bestMatch = tracked
            }
        }

        return bestMatch
    }

    private fun updateMissedFrames(matchedTrackedIds: Set<Long>) {
        val iterator = trackedBlocks.listIterator()

        while (iterator.hasNext()) {
            val tracked = iterator.next()

            if (tracked.id in matchedTrackedIds)
                continue

            val updated = tracked.copy(missedFrames = tracked.missedFrames + 1)

            if (updated.missedFrames > MAX_MISSED_FRAMES)
                iterator.remove()
            else
                iterator.set(updated)
        }
    }

    private fun smoothBounds(previous: TextBounds, current: TextBounds): TextBounds {
        return TextBounds(
            left = lerp(previous.left, current.left),
            top = lerp(previous.top, current.top),
            right = lerp(previous.right, current.right),
            bottom = lerp(previous.bottom, current.bottom)
        )
    }

    private fun lerp(previous: Int, current: Int): Int =
        (previous + (current - previous) * BOUNDS_SMOOTHING_ALPHA).toInt()

    private fun calculateIoU(first: TextBounds, second: TextBounds): Float {
        val left = maxOf(first.left, second.left)
        val top = maxOf(first.top, second.top)
        val right = minOf(first.right, second.right)
        val bottom = minOf(first.bottom, second.bottom)

        val intersectionWidth = (right - left).coerceAtLeast(0)
        val intersectionHeight = (bottom - top).coerceAtLeast(0)
        val intersectionArea = intersectionWidth * intersectionHeight

        if (intersectionArea <= 0)
            return 0f

        val firstArea = (first.right - first.left)
            .coerceAtLeast(0) * (first.bottom - first.top)
            .coerceAtLeast(0)

        val secondArea = (second.right - second.left)
            .coerceAtLeast(0) * (second.bottom - second.top)
            .coerceAtLeast(0)

        val unionArea = firstArea + secondArea - intersectionArea

        if (unionArea <= 0)
            return 0f

        return intersectionArea.toFloat() / unionArea.toFloat()
    }

    private fun calculateTextSimilarity(first: String, second: String): Float {
        if (first == second)
            return 1f

        if (first.isBlank() || second.isBlank())
            return 0f

        val maxLength = maxOf(first.length, second.length)
        val distance = levenshteinDistance(first = first, second = second)

        return 1f - distance.toFloat() / maxLength.toFloat()
    }

    private fun levenshteinDistance(first: String, second: String): Int {
        if (first == second)
            return 0

        if (first.isEmpty())
            return second.length

        if (second.isEmpty())
            return first.length

        var previousRow = IntArray(second.length + 1) { it }
        var currentRow = IntArray(second.length + 1)

        for (i in first.indices) {
            currentRow[0] = i + 1

            for (j in second.indices) {
                val insertion = currentRow[j] + 1
                val deletion = previousRow[j + 1] + 1
                val substitution = previousRow[j] + if (first[i] == second[j]) 0 else 1

                currentRow[j + 1] = minOf(insertion, deletion, substitution)
            }

            val temp = previousRow

            previousRow = currentRow
            currentRow = temp
        }

        return previousRow[second.length]
    }

    private fun normalize(text: String): String =
        text
            .trim()
            .lowercase()
            .replace(Regex("\\s+"), " ")

    private data class TrackedBlock(
        val id: Long,
        val stableText: String,
        val candidateText: String,
        val candidateDetections: Int,
        val bounds: TextBounds,
        val detections: Int,
        val missedFrames: Int,
    )
}