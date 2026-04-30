package com.example.translatorapp.presentation.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.translatorapp.R
import com.example.translatorapp.presentation.ui.theme.Grey
import com.example.translatorapp.presentation.ui.theme.MainColor
import com.example.translatorapp.presentation.ui.theme.White

// Login Components
@Composable
fun LoginHeaderImage() {
    Image(
        painter = painterResource(id = R.drawable.img_login),
        contentDescription = "Auth Image",
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1.1f)
    )
}

@Composable
fun LoginTitle() {
    Text(
        text = "Sign in",
        fontSize = 32.sp,
        fontWeight = FontWeight.SemiBold
    )
}

@Composable
fun LoginForm(
    email: String,
    errorText: String?,
    onEmailChange: (String) -> Unit,
    password: String,
    onPasswordChange: (String) -> Unit,
    passwordVisible: Boolean,
    onPasswordVisibilityChange: () -> Unit,
    onLoginClick: () -> Unit,
    onGoogleLoginClick: () -> Unit,
    onSignUpClick: () -> Unit,
    onPasswordResetClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        EmailField(email = email, onEmailChange = onEmailChange, errorText = errorText)

        Spacer(Modifier.height(16.dp))

        PasswordField(
            password = password,
            onPasswordChange = onPasswordChange,
            passwordVisible = passwordVisible,
            onPasswordVisibilityChange = onPasswordVisibilityChange,
            errorText = errorText
        )

        Spacer(Modifier.height(4.dp))

        ForgotPasswordText(onPasswordResetClick = onPasswordResetClick)

        Spacer(Modifier.height(24.dp))

        LoginButton(onLoginClick = onLoginClick)

        Spacer(Modifier.height(46.dp))

        GoogleLoginButton(onGoogleLoginClick = onGoogleLoginClick)

        Spacer(Modifier.height(18.dp))

        SignUpRow(onSignUpClick = onSignUpClick)
    }
}

@Composable
private fun EmailField(
    email: String,
    onEmailChange: (String) -> Unit,
    errorText: String?,
) {
    OutlinedTextField(
        value = email,
        onValueChange = onEmailChange,
        label = { Text("Enter your email") },
        singleLine = true,
        leadingIcon = {
            Icon(
                painter = painterResource(id = R.drawable.ic_mail),
                contentDescription = "Email Icon"
            )
        },
        isError = (errorText != null),
        shape = RoundedCornerShape(14.dp),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
private fun PasswordField(
    password: String,
    errorText: String?,
    onPasswordChange: (String) -> Unit,
    passwordVisible: Boolean,
    onPasswordVisibilityChange: () -> Unit,
) {
    OutlinedTextField(
        value = password,
        onValueChange = onPasswordChange,
        label = { Text("Enter your password") },
        singleLine = true,
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier.fillMaxWidth(),
        visualTransformation =
            if (passwordVisible) VisualTransformation.None
            else PasswordVisualTransformation(),
        leadingIcon = {
            Icon(
                painter = painterResource(id = R.drawable.ic_lock),
                contentDescription = "Password Icon"
            )
        },
        trailingIcon = {
            val icon =
                if (passwordVisible) painterResource(id = R.drawable.ic_eye_visible)
                else painterResource(id = R.drawable.ic_eye_invisible)

            IconButton(onClick = {
                onPasswordVisibilityChange()
            }) {
                Icon(
                    painter = icon,
                    contentDescription = "Visibility Icon"
                )
            }
        },
        isError = (errorText != null),
        supportingText = {
            if (errorText != null)
                Text(
                    text = errorText,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium
                )
        }
    )
}

@Composable
private fun ConfirmPasswordField(
    confirmPassword: String,
    onConfirmPasswordChange: (String) -> Unit,
    confirmPasswordVisible: Boolean,
    onConfirmPasswordVisibilityChange: () -> Unit,
    errorText: String?,
) {
    OutlinedTextField(
        value = confirmPassword,
        onValueChange = onConfirmPasswordChange,
        label = { Text("Confirm password") },
        singleLine = true,
        shape = RoundedCornerShape(14.dp),
        visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        leadingIcon = {
            Icon(
                painter = painterResource(id = R.drawable.ic_lock),
                contentDescription = null
            )
        },
        trailingIcon = {
            IconButton(onClick = { onConfirmPasswordVisibilityChange() }) {
                Icon(
                    painter = painterResource(
                        id = if (confirmPasswordVisible)
                            R.drawable.ic_eye_visible
                        else R.drawable.ic_eye_invisible
                    ),
                    contentDescription = "Toggle password visibility"
                )
            }
        },
        isError = (errorText != null),
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
private fun ForgotPasswordText(onPasswordResetClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End
    ) {
        Text(
            text = "Forgot password?",
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = MainColor,
            modifier = Modifier.clickable { onPasswordResetClick() }
        )
    }
}

@Composable
private fun LoginButton(
    onLoginClick: () -> Unit,
) {
    Button(
        onClick = { onLoginClick() },
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MainColor,
            contentColor = White
        ),
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
    ) {
        Text(
            text = "Login",
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun RegisterButton(
    onRegisterClick: () -> Unit,
) {
    Button(
        onClick = { onRegisterClick() },
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MainColor,
            contentColor = White
        ),
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
    ) {
        Text(
            text = "Create account",
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun GoogleLoginButton(onGoogleLoginClick: () -> Unit) {
    OutlinedButton(
        onClick = onGoogleLoginClick,
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
    ) {

        Icon(
            painter = painterResource(id = R.drawable.ic_google),
            contentDescription = "Google Icon",
            modifier = Modifier.size(20.dp),
            tint = Color.Unspecified
        )

        Box(
            modifier = Modifier.weight(1f),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Sign in with Google",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = Grey
            )
        }

        Spacer(modifier = Modifier.size(20.dp))
    }
}

@Composable
private fun SignUpRow(onSignUpClick: () -> Unit) {
    Row {
        Text(
            text = "Don't have an account? ",
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = Grey
        )
        Text(
            text = "Sign up",
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = MainColor,
            modifier = Modifier.clickable { onSignUpClick() }
        )
    }
}

// Registration Components
@Composable
fun RegistrationHeaderImage() {
    Image(
        painter = painterResource(id = R.drawable.img_register),
        contentDescription = "Auth Image",
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1.1f)
    )
}

@Composable
fun RegistrationTitle() {
    Text(
        text = "Sign up",
        fontSize = 32.sp,
        fontWeight = FontWeight.SemiBold
    )
}

@Composable
fun RegistrationForm(
    email: String,
    onEmailChange: (String) -> Unit,
    password: String,
    errorText: String?,
    onPasswordChange: (String) -> Unit,
    passwordVisible: Boolean,
    onPasswordVisibilityChange: () -> Unit,
    confirmPassword: String,
    onConfirmPasswordChange: (String) -> Unit,
    confirmPasswordVisible: Boolean,
    onConfirmPasswordVisibilityChange: () -> Unit,
    onRegisterClick: () -> Unit,
    onLoginClick: () -> Unit,
) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        EmailField(email = email, onEmailChange = onEmailChange, errorText = errorText)

        Spacer(Modifier.height(16.dp))

        PasswordField(
            password = password,
            onPasswordChange = onPasswordChange,
            passwordVisible = passwordVisible,
            onPasswordVisibilityChange = onPasswordVisibilityChange,
            errorText = errorText
        )

        Spacer(Modifier.height(16.dp))

        ConfirmPasswordField(
            confirmPassword = confirmPassword,
            onConfirmPasswordChange = onConfirmPasswordChange,
            confirmPasswordVisible = confirmPasswordVisible,
            onConfirmPasswordVisibilityChange = onConfirmPasswordVisibilityChange,
            errorText = errorText
        )

        Spacer(Modifier.height(24.dp))

        RegisterButton(
            onRegisterClick = onRegisterClick
        )

        Spacer(Modifier.height(32.dp))

        Row {
            Text(
                "Already have an account? ",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Grey
            )
            Text(
                text = "Login",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = MainColor,
                modifier = Modifier.clickable { onLoginClick() }
            )
        }
    }
}