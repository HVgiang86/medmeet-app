package com.huongmt.medmeet.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridItemScope
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.dokar.sonner.Toaster
import com.dokar.sonner.rememberToasterState
import com.huongmt.medmeet.R
import com.huongmt.medmeet.component.ConfirmChatBotSheet
import com.huongmt.medmeet.component.NotFoundCard
import com.huongmt.medmeet.component.PrimaryButton
import com.huongmt.medmeet.component.ScrollableDatePicker
import com.huongmt.medmeet.data.WholeApp
import com.huongmt.medmeet.shared.app.HomeAction
import com.huongmt.medmeet.shared.app.HomeState
import com.huongmt.medmeet.shared.app.HomeStore
import com.huongmt.medmeet.shared.utils.ext.nowDate
import com.huongmt.medmeet.theme.Grey_500
import com.huongmt.medmeet.ui.home.list.ClinicItem
import com.huongmt.medmeet.ui.home.list.ClinicItemShimmer
import com.huongmt.medmeet.ui.home.list.HomeItemType
import com.huongmt.medmeet.ui.home.list.ScheduleItem
import com.huongmt.medmeet.ui.home.list.getClinicItemIndex
import com.huongmt.medmeet.ui.home.list.getItemList
import com.huongmt.medmeet.ui.main.nav.MainScreenDestination
import io.github.aakira.napier.Napier
import kotlinx.datetime.LocalDate


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    store: HomeStore, navigateTo: (MainScreenDestination) -> Unit, onLogout: () -> Unit = {},
) {
    val state by store.observeState().collectAsState()

    val toasterState = rememberToasterState()
    var isRefreshing by remember { mutableStateOf(false) }
    val pullToRefreshState = rememberPullToRefreshState()

    LaunchedEffect(Unit) {
        store.sendAction(HomeAction.LoadUser)
        store.sendAction(HomeAction.LoadClinics)
        store.sendAction(HomeAction.LoadAppointments(nowDate()))
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Toaster(state = toasterState)
        PullToRefreshBox(modifier = Modifier.fillMaxSize(),
            state = pullToRefreshState,
            isRefreshing = false,
            onRefresh = {
                store.sendAction(HomeAction.LoadUser)
                store.sendAction(HomeAction.LoadClinics)
                store.sendAction(HomeAction.LoadAppointments(nowDate()))
            }) {

            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                val itemList = getItemList(
                    clinicsAmount = state.clinics.size
                )
                items(itemList.size) {
                    val listSize = itemList.size
                    when (itemList[it]) {
                        HomeItemType.HEADER -> {
                            HeaderView(
                                store = store, state = state, navigateTo = navigateTo
                            )
                        }

                        HomeItemType.CLINIC_ITEM -> {
                            val itemIndex = getClinicItemIndex(it, listSize)
                            Napier.d { "itemIndex: $itemIndex" }
                            val clinic = state.clinics[itemIndex]
                            ClinicItem(modifier = Modifier
                                .wrapContentHeight()
                                .padding(horizontal = 24.dp),
                                clinic = clinic,
                                onClick = { clinicNav ->
                                    navigateTo(MainScreenDestination.ClinicDetail(clinicNav))
                                })
                        }

                        HomeItemType.NO_CLINICS -> {
                            if (state.isClinicLoading) {
                                ClinicItemShimmer()
                            } else {
                                NotFoundCard(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .wrapContentHeight()
                                        .wrapContentHeight()
                                        .padding(horizontal = 24.dp),
                                    text = "Không tìm thấy phòng khám!"
                                )
                            }
                        }

                        HomeItemType.PADDING_BOTTOM -> {
                            Spacer(modifier = Modifier.height(80.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun HeaderView(
    store: HomeStore,
    state: HomeState,
    navigateTo: (MainScreenDestination) -> Unit = {},
) {
    Box(
        modifier = Modifier
            .aspectRatio(1.0f)
            .fillMaxWidth()
    ) {
        val openConfirmChatBotSheet = remember { mutableStateOf(false) }

        Image(
            modifier = Modifier.fillMaxSize(),
            painter = painterResource(R.drawable.png_home_bg),
            contentDescription = "Banner",
        )

        Column(modifier = Modifier.fillMaxSize()) {
            HomeHeaderBar(modifier = Modifier.padding(top = 52.dp),
                state = state,
                onProfileClick = {
                    navigateTo(MainScreenDestination.Profile())
                })
            Column(
                modifier = Modifier
                    .padding(vertical = 32.dp)
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth(0.5f)
                    .fillMaxHeight(), verticalArrangement = Arrangement.Top
            ) {

                Spacer(modifier = Modifier.weight(1f))

                Column(modifier = Modifier.wrapContentHeight()) {
                    Text(
                        text = "Welcome!\n${state.user?.name ?: ""}",
                        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                        maxLines = 3
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Chúc bạn một ngày tốt lành 😘",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Grey_500,
                        maxLines = 2
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                Row(
                    modifier = Modifier
                        .wrapContentHeight()
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    PrimaryButton(onClick = {
                        openConfirmChatBotSheet.value = true
                    }, text = {
                        Text(
                            "Trợ lý A.I",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White
                        )
                    }, modifier = Modifier.padding(horizontal = 4.dp))
                }

                Spacer(modifier = Modifier.weight(0.5f))
            }
        }

        if (openConfirmChatBotSheet.value) {
            if (!WholeApp.confirmChatBot) {
                ConfirmChatBotSheet(onConfirm = {
                    navigateTo(MainScreenDestination.ChatScreen)
                    openConfirmChatBotSheet.value = false
                    WholeApp.confirmChatBot = true
                }, onDismiss = {
                    openConfirmChatBotSheet.value = false
                })
            } else {
                navigateTo(MainScreenDestination.ChatScreen)
                openConfirmChatBotSheet.value = false
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .offset(y = (-32).dp)
            .background(
                color = MaterialTheme.colorScheme.background,
                shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
            )
    ) {
        HorizontalScheduleView {
            store.sendAction(HomeAction.ShowAppointmentDay(it))
        }

        if (state.displayAppointments != null) {
            ScheduleItem(modifier = Modifier.padding(horizontal = 24.dp),
                schedule = state.displayAppointments!!,
                onClick = {
                    navigateTo(MainScreenDestination.BookingDetail(state.displayAppointments!!.id))
                })
        } else {
            NotFoundCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(horizontal = 24.dp),
                text = "Không có lịch trình nào!"
            )
        }

        ClinicListLabel()
    }
}

@Composable
fun HorizontalScheduleView(
    onDateSelected: (LocalDate) -> Unit = {},
) {
    Column(
        modifier = Modifier
            .wrapContentHeight()
            .padding(horizontal = 24.dp)
            .padding(top = 16.dp)
    ) {
        ScrollableDatePicker(onDateSelected = {
            onDateSelected(it)
        })

        Spacer(modifier = Modifier.height(16.dp))

        Text("Lịch khám", style = MaterialTheme.typography.titleMedium)

        Spacer(modifier = Modifier.height(16.dp))


    }
}

@Composable
fun ClinicListLabel(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .padding(24.dp)
            .fillMaxWidth()
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Tìm phòng khám", style = MaterialTheme.typography.titleMedium)
//            Text("Xem thêm",
//                style = MaterialTheme.typography.titleMedium,
//                color = Grey_500,
//                modifier = Modifier.clickable {
//
//                })
        }
    }
}

@Composable
fun HomeHeaderBar(
    modifier: Modifier = Modifier,
    state: HomeState,
    onSearchClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        AsyncImage(
            model = state.user?.avatar ?: "",
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .align(Alignment.CenterVertically)
                .clickable {
                    onProfileClick()
                },
            error = painterResource(R.drawable.ic_default_avatar),
            contentScale = ContentScale.Crop,
            contentDescription = null
        )

        Spacer(modifier = Modifier.weight(1f))

        IconButton(
            onClick = onSearchClick,
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .align(Alignment.CenterVertically)
                .background(color = Color.White, shape = CircleShape),
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

fun LazyGridScope.header(
    content: @Composable LazyGridItemScope.() -> Unit,
) {
    item(span = { GridItemSpan(this.maxLineSpan) }, content = content)
}

@Composable
fun HeaderRow(title: String, onShowMoreClicked: () -> Unit = {}) {
    Box(
        modifier = Modifier
            .padding(start = 8.dp, end = 8.dp)
            .fillMaxWidth()
            .wrapContentHeight()
    ) {
        Text(
            modifier = Modifier.align(Alignment.CenterStart),
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = Color.Black
        )
//        Text(
//            modifier = Modifier
//                .align(Alignment.CenterEnd)
//                .clickable {
//                    onShowMoreClicked()
//                },
//            text = "Xem thêm",
//            style = MaterialTheme.typography.titleSmall,
//            color = Color.Gray
//        )
    }
}
