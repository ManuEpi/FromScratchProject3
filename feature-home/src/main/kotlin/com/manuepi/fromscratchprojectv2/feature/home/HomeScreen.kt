package com.manuepi.fromscratchprojectv2.feature.home

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.integration.compose.placeholder
import com.manuepi.fromscratchprojectv2.common.Screens
import com.manuepi.fromscratchprojectv2.common.SharedNavigator
import com.manuepi.fromscratchprojectv2.common.formatText
import com.manuepi.fromscratchprojectv2.feature.home.model.NewsItemUiModel
import com.manuepi.fromscratchprojectv2.feature.home.model.HomeUiStateModel
import com.manuepi.fromscratchprojectv2.feature.home.model.NewsUiModel

@SuppressLint("PrivateResource")
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    sharedNavigator: SharedNavigator
) {
    val state = viewModel.viewState.collectAsStateWithLifecycle()

    // API call
    LaunchedEffect(key1 = Unit) {
        viewModel.getNews()
    }

    when (val result = state.value) {
        HomeUiStateModel.State.Failure -> {
            FailureState()
        }
        HomeUiStateModel.State.Init -> {
            // NO-OP
        }
        HomeUiStateModel.State.Loading -> {
            LoadingState()
        }
        is HomeUiStateModel.State.Success -> {
            SuccessState(
                model = result.model,
                viewModel = viewModel,
                sharedNavigator = sharedNavigator
            )
        }
    }
}

@Composable
fun LoadingState() {
    Text(
        text = "Chargement des articles en cours", modifier = Modifier
            .padding(12.dp), fontSize = 20.sp, fontFamily = FontFamily.SansSerif
    )
}

@Composable
fun FailureState() {
    Text(
        text = "Echec du chargement des articles. Le nombre maximal d'appel à l'API à été atteint.",
        modifier = Modifier
            .padding(12.dp),
        fontSize = 20.sp,
        fontFamily = FontFamily.SansSerif
    )
}

@Composable
fun SuccessState(model: NewsUiModel, viewModel: HomeViewModel, sharedNavigator: SharedNavigator) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp)
    ) {
        if (model.totalResults == null || model.totalResults == 0) {
            Text(
                text = "Malheureusement aucun article n'a été trouvé, veuillez réessayer plus tard",
                modifier = Modifier
                    .padding(12.dp),
                fontSize = 20.sp,
                fontFamily = FontFamily.SansSerif
            )
        } else {
            Text(
                text = formatText(model.totalResults), modifier = Modifier
                    .padding(12.dp), fontSize = 20.sp, fontFamily = FontFamily.SansSerif
            )

            LazyColumn {
                items(model.articles) { modelUi ->
                    HomeItems(modelUi = modelUi, onItemClicked = {
                        viewModel.updateSelectedNews(modelUi = modelUi)
                        sharedNavigator.updateScreenState(screen = Screens.NewsDetail)
                    })
                }
            }
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun HomeItems(modelUi: NewsItemUiModel, onItemClicked: () -> Unit) {
    Spacer(modifier = Modifier.padding(8.dp))
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .background(color = Color.LightGray, shape = RoundedCornerShape(12.dp))
            .clickable { onItemClicked.invoke() }
            .fillMaxSize()
            .padding(12.dp),
    ) {
        GlideImage(
            model = modelUi.urlToImage,
            contentScale = ContentScale.Crop,
            contentDescription = modelUi.urlToImage,
            failure = placeholder(androidx.core.R.drawable.ic_call_answer),
            modifier = Modifier.size(60.dp)
        )
        Column(verticalArrangement = Arrangement.Center, modifier = Modifier.padding(12.dp)) {
            Text(
                text = modelUi.title.toString(),
                maxLines = 2,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = modelUi.description.toString(),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}