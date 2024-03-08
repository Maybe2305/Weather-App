package ru.netology.weatherapp.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import ru.netology.weatherapp.data.WeatherModel
import ru.netology.weatherapp.ui.theme.Phone


@Composable
fun MainList(list: List<WeatherModel>, currentDay: MutableState<WeatherModel>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {

        itemsIndexed(
            list
        ) {
                _, item -> ListItem(item, currentDay)
        }

    }
}

@Composable
fun ListItem(item: WeatherModel, currentDay: MutableState<WeatherModel>) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(5.dp)
            .clickable {
                if (item.hours.isEmpty()) return@clickable
                currentDay.value = item
            },
        colors = CardDefaults.cardColors(Phone),
        elevation = CardDefaults.cardElevation(0.dp),
        shape = RoundedCornerShape(10.dp)
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Column(
                modifier = Modifier
                    .padding(start = 7.dp, top = 5.dp, bottom = 5.dp)
            ) {

                Text(
                    text = item.time,
                    color = Color.White,
                )
                Text(
                    text = item.conditionText,
                    color = Color.White,
                )

            }

            Text(
                text = item.currentTemp.ifEmpty { "${item.minTemp}/${item.maxTemp}" },
                color = Color.White,
                fontSize = 20.sp
            )

            AsyncImage(
                model = "https:${item.conditionIcon}",
                contentDescription = "im5",
                modifier = Modifier
                    .size(40.dp)
                    .padding(end = 7.dp)
            )


        }

    }

}

@Composable
fun DialogSearch(dialogState: MutableState<Boolean>, onSubmit: (String) -> Unit) {
    val dialogText = remember {
        mutableStateOf("")
    }

    AlertDialog(onDismissRequest = {
        dialogState.value = false
    },
        confirmButton = {
            onSubmit(dialogText.value)
            TextButton(onClick = { dialogState.value = false }) {
                Text(text = "OK")
            }
        },
        dismissButton = {
            TextButton(onClick = { dialogState.value = false }) {
                Text(text = "CANCEL")
            }
        },

        title = {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Введите название города:")
                TextField(value = dialogText.value, onValueChange = { dialogText.value = it })
            }
        }
        )
}