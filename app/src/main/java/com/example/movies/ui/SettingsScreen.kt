package com.example.movies.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.movies.R
import com.example.movies.data.supportedLanguages

@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize()
    ) {
        Image(
            painter = painterResource(R.drawable.movies),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        LazyColumn(
            modifier = Modifier
        ) {
//            LanguageOption()
//            TextScaleOption()
        }
    }
}


@Composable
fun LanguageOption(
    selectedLanguageCode: String,
    onSelectedLanguage: (String) -> Unit
) {
    SettingCard(title = stringResource(R.string.setting_language)) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
        ) {
            supportedLanguages.forEach { language ->
                val isSelected = language.languageCode == selectedLanguageCode

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp, horizontal = 16.dp)
                        .border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.primary,
                            shape = RoundedCornerShape(12.dp)
                        )
                ) {
                    RadioButton(
                        selected = isSelected,
                        onClick = { onSelectedLanguage(language.languageCode) }
                    )

                    // TODO: language flag
                    //Image()

                    Text(
                        text = language.language,
                        modifier = Modifier.padding(start = 12.dp),
                        color = if (isSelected) MaterialTheme.colorScheme.primary else Color.White
                    )
                }
            }
        }
    }
}

@Composable
fun TextScaleOption() {
    SettingCard(title = stringResource(R.string.setting_text_scale)) {
//        TODO: text scale setting
    }
}


@Composable
fun SettingCard(
    title: String = stringResource(R.string.setting_language),
    content: @Composable () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .padding(start = 28.dp, end = 28.dp, bottom = 12.dp)
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .height(50.dp)
                    .fillMaxWidth(),
                contentAlignment = Alignment.CenterStart
            ) {
                Text(
                    text = title,
                    modifier = Modifier.padding(start = 25.dp)
                )
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth(),
                colors = CardDefaults.cardColors(Color.Black)
            ) {
                content()
            }
        }
    }
}

@Preview
@Composable
fun SettingCardPreview() {
    LanguageOption("ru") { }
}

@Preview(showSystemUi = true)
@Composable
fun SettingsScreenPreview() {
    SettingsScreen()
}