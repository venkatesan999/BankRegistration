package com.example.bankregistration.ui

import android.app.Activity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bankregistration.R
import com.example.bankregistration.model.PanUseCaseRepository
import com.example.bankregistration.ui.theme.BankRegistrationTheme
import com.example.bankregistration.viewmodel.MainActivityViewModel
import com.example.bankregistration.viewmodel.PanViewModelFactory

class MainActivity : ComponentActivity() {
    private val viewModel: MainActivityViewModel by viewModels {
        PanViewModelFactory(PanUseCaseRepository())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BankRegistrationTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    UiElements(innerPadding, viewModel)
                }
            }
        }
    }
}

@Composable
fun UiElements(innerPadding: PaddingValues, viewModel: MainActivityViewModel?) {
    val panNumber = remember { mutableStateOf("") }
    val month = remember { mutableStateOf("") }
    val date = remember { mutableStateOf("") }
    val year = remember { mutableStateOf("") }
    val context = LocalContext.current
    val verificationResultState = viewModel?.verificationResult?.observeAsState(initial = false)
    val errorMessage = viewModel?.errorMessage?.observeAsState(null)
    val isVerifyPan = remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .padding(40.dp),
        horizontalAlignment = Alignment.Start
    ) {

        // Bank registration use case handling here
        LaunchedEffect(
            panNumber.value,
            month.value,
            date.value,
            year.value
        ) {
            if (panNumber.value.length == 10 && year.value.length == 4 && date.value.isNotEmpty() && month.value.isNotEmpty()) {
                viewModel?.verifyPan(
                    panNumber.value,
                    date.value,
                    month.value,
                    year.value
                )
                verificationResultState?.value?.let { result ->
                    isVerifyPan.value = result
                }
                errorMessage?.value?.let { errorMessage ->
                    Toast.makeText(
                        context,
                        errorMessage,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                isVerifyPan.value = false
            }
        }

        // Ui components here
        // Top
        Text(
            text = "S.",
            style = TextStyle(
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Default,
                color = Color.Red
            )
        )
        Spacer(modifier = Modifier.height(26.dp))
        Text(
            text = stringResource(id = R.string.registration_header),
            style = TextStyle(
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Default
            )
        )
        // Center
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = stringResource(id = R.string.pan_number_label),
                style = TextStyle(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    fontFamily = FontFamily.Default
                )
            )
            Spacer(modifier = Modifier.height(10.dp))
            PanNumberTextField {
                panNumber.value = it
            }
            Spacer(modifier = Modifier.height(30.dp))
            Text(
                text = stringResource(id = R.string.birthdate_label),
                style = TextStyle(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    fontFamily = FontFamily.Default
                )
            )
            Spacer(modifier = Modifier.height(10.dp))
            Row(modifier = Modifier.fillMaxWidth()) {
                DateTextField {
                    date.value = it
                }
                Spacer(modifier = Modifier.width(20.dp))
                MonthTextField {
                    month.value = it
                }
                Spacer(modifier = Modifier.width(20.dp))
                YearTextField {
                    year.value = it
                }
            }
        }
        // Bottom
        Spacer(modifier = Modifier.height(20.dp))
        HelpText()
        Spacer(modifier = Modifier.height(10.dp))
        isVerifyPan.value.let { isValidPan ->
            Button(
                onClick = {
                    Toast.makeText(
                        context,
                        context.getString(R.string.success),
                        Toast.LENGTH_SHORT
                    ).show()
                    (context as? Activity)?.finish()
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    if (isValidPan) colorResource(id = R.color.purple_500) else Color.Gray,
                    contentColor = Color.White
                ),
                enabled = isValidPan // enable/disable the button
            ) {
                Text(text = stringResource(id = R.string.next_btn))
            }
        }

        Spacer(modifier = Modifier.height(10.dp))
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { (context as? Activity)?.finish() },
            text = stringResource(id = R.string.i_do_not_have_a_pan),
            style = TextStyle(
                color = colorResource(id = R.color.purple_500),
                textAlign = TextAlign.Center
            )
        )
    }
}

@Composable
fun PanNumberTextField(
    cardDetails: (panNumber: String) -> Unit
) {
    var panNumber by remember { mutableStateOf("") }
    Box(
        modifier = Modifier
            .border(
                width = 1.dp,
                color = Color.Gray,
                shape = RoundedCornerShape(10.dp)
            )
            .background(
                color = colorResource(id = R.color.white), // You can use Color.Gray for this
                shape = RoundedCornerShape(10.dp)
            )
    ) {
        OutlinedTextField(
            value = panNumber,
            onValueChange = { newValue ->
                if (newValue.length <= 10) {
                    panNumber = newValue.filter { it.isLetterOrDigit() }
                    cardDetails(panNumber)
                }
            },
            placeholder = {
                Text(
                    "XXXXXXXXXX",
                    style = TextStyle(
                        fontFamily = FontFamily.Default,
                        fontSize = 16.sp,
                        color = Color(0x40000000),
                        textAlign = TextAlign.Center
                    )
                )
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent,
                cursorColor = Color.Black
            ),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Done,
                capitalization = KeyboardCapitalization.Characters
            ), textStyle = TextStyle(
                fontFamily = FontFamily.Default,
                fontSize = 16.sp
            )
        )
    }
}

@Composable
fun MonthTextField(
    monthValue: (panNumber: String) -> Unit
) {
    var month by remember { mutableStateOf("") }
    Box(
        modifier = Modifier
            .border(
                width = 1.dp,
                color = Color.Gray,
                shape = RoundedCornerShape(10.dp)
            )
            .background(
                color = colorResource(id = R.color.white), // You can use Color.Gray for this
                shape = RoundedCornerShape(10.dp)
            )
            .width(60.dp)
    ) {
        OutlinedTextField(
            value = month,
            onValueChange = {
                if (it.length <= 2) {
                    month = it
                    monthValue(it)
                }
            },
            placeholder = {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "MM",
                        style = TextStyle(
                            fontFamily = FontFamily.Default,
                            fontSize = 16.sp,
                            color = Color(0x40000000),
                            textAlign = TextAlign.Center
                        )
                    )
                }
            },
            textStyle = TextStyle(
                textAlign = TextAlign.Center,
                fontFamily = FontFamily.Default,
                fontSize = 16.sp
            ),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent,
                cursorColor = Color.Black
            ),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
            ),
        )
    }
}

@Composable
fun DateTextField(
    dateValue: (panNumber: String) -> Unit
) {
    var date by remember { mutableStateOf("") }
    Box(
        modifier = Modifier
            .border(
                width = 1.dp,
                color = Color.Gray,
                shape = RoundedCornerShape(10.dp)
            )
            .background(
                color = colorResource(id = R.color.white), // You can use Color.Gray for this
                shape = RoundedCornerShape(10.dp)
            )
            .width(60.dp)
    ) {
        OutlinedTextField(
            value = date,
            onValueChange = {
                if (it.length <= 2) {
                    date = it
                    dateValue(it)
                }
            },
            placeholder = {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "DD",
                        style = TextStyle(
                            fontFamily = FontFamily.Default,
                            fontSize = 16.sp,
                            color = Color(0x40000000),
                            textAlign = TextAlign.Center
                        )
                    )
                }
            },
            textStyle = TextStyle(
                textAlign = TextAlign.Center,
                fontFamily = FontFamily.Default,
                fontSize = 16.sp
            ),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent,
                cursorColor = Color.Black
            ),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
            )
        )
    }
}

@Composable
fun YearTextField(
    yearValue: (panNumber: String) -> Unit
) {
    var year by remember { mutableStateOf("") }
    Box(
        modifier = Modifier
            .border(
                width = 1.dp,
                color = Color.Gray,
                shape = RoundedCornerShape(10.dp)
            )
            .background(
                color = colorResource(id = R.color.white), // You can use Color.Gray for this
                shape = RoundedCornerShape(10.dp)
            )
            .width(100.dp)
    ) {
        OutlinedTextField(
            value = year,
            onValueChange = {
                if (it.length <= 4) {
                    year = it
                    yearValue(it)
                }
            },
            placeholder = {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "YYYY",
                        style = TextStyle(
                            fontFamily = FontFamily.Default,
                            fontSize = 16.sp,
                            color = Color(0x40000000),
                            textAlign = TextAlign.Center
                        )
                    )
                }
            },
            textStyle = TextStyle(
                textAlign = TextAlign.Center,
                fontFamily = FontFamily.Default,
                fontSize = 16.sp
            ),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent,
                cursorColor = Color.Black
            ),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
            )
        )
    }
}

@Composable
fun HelpText() {
    val annotatedString = buildAnnotatedString {
        append("Providing PAN & Date of Birth helps us find and fetch your KYC from a central registry by the Government of India. ")
        pushStringAnnotation(
            tag = "LearnMore",
            annotation = "Learn more"
        )
        append("Learn more")
        pop()
    }
    val spanStyle = SpanStyle(color = colorResource(id = R.color.purple_500))

    val annotatedClickableString = AnnotatedString.Builder(annotatedString)
        .apply {
            addStyle(
                style = spanStyle,
                start = annotatedString.indexOf("Learn more"),
                end = annotatedString.indexOf("Learn more") + "Learn more".length
            )
        }
        .toAnnotatedString()

    ClickableText(
        text = annotatedClickableString,
        onClick = { offset ->
            annotatedString.getStringAnnotations(
                tag = "LearnMore",
                start = offset,
                end = offset
            ).firstOrNull()?.let { annotation ->
                // Handle click on "Learn more" here
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    BankRegistrationTheme {
        UiElements(PaddingValues(0.dp), null)
    }
}