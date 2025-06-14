package com.huongmt.medmeet.shared.app

import com.huongmt.medmeet.shared.base.Store
import com.huongmt.medmeet.shared.core.entity.Conversation
import com.huongmt.medmeet.shared.core.entity.Message
import com.huongmt.medmeet.shared.core.entity.MedicalService
import com.huongmt.medmeet.shared.core.entity.Clinic
import com.huongmt.medmeet.shared.core.repository.ChatRepository
import com.huongmt.medmeet.shared.utils.ext.nowDateTime
import io.github.aakira.napier.Napier

data class ChatState(
    val messages: List<Message> = emptyList(),
    val conversationList: List<Conversation> = emptyList(),
    val currentConversationId: String? = null,
    val isGenerating: Boolean = false,
    val isLoading: Boolean = false,
    val error: Throwable? = null,
    val isGenQueriesEnabled: Boolean = true,
    val recommendedQueries: List<String> = emptyList(),
    val isLoadingQueries: Boolean = true,
    val medicalServices: List<MedicalService> = emptyList(),
    val selectedMedicalService: MedicalService? = null,
    val showMedicalServiceBottomSheet: Boolean = false,
    val medicalServiceClinic: Clinic? = null
) : Store.State(loading = isLoading)

sealed interface ChatAction : Store.Action {
    data class SendMessage(
        val text: String,
        val conversationId: String? = null
    ) : ChatAction

    data object NewConversation : ChatAction

    data class GetConversationList(val showLatest: Boolean = false) : ChatAction

    data class SelectConversation(
        val conversationId: String
    ) : ChatAction

    data class GetConversationListSuccess(
        val conversations: List<Conversation>,
        val showLatest: Boolean
    ) : ChatAction

    data object GetConversationListError : ChatAction

    data class GetMessageHistory(
        val conversationId: String
    ) : ChatAction

    data class SendMessageSuccess(
        val message: Message,
        val conversationId: String? = null
    ) : ChatAction

    data class GetMessageHistorySuccess(
        val messages: List<Message>,
        val conversationId: String
    ) : ChatAction

    data class NewConversationSuccess(
        val conversation: Conversation
    ) : ChatAction

    data class Error(
        val error: Throwable
    ) : ChatAction

    data class DeleteConversation(
        val conversationId: String
    ) : ChatAction

    data class DeleteConversationSuccess(
        val conversationId: String
    ) : ChatAction

    data object ClearError : ChatAction

    data class ToggleGenQueries(val enabled: Boolean) : ChatAction

    data class GetRecommendedQueries(val conversationId: String) : ChatAction

    data class GetRecommendedQueriesSuccess(val queries: List<String>) : ChatAction

    data class SelectMedicalService(val service: MedicalService) : ChatAction
    data object HideMedicalServiceBottomSheet : ChatAction
    data class LoadMedicalServiceClinic(val clinicId: String) : ChatAction
    data class LoadMedicalServiceClinicSuccess(val clinic: Clinic) : ChatAction
}

sealed interface ChatEffect : Store.Effect {
    data class ShowToast(
        val message: String
    ) : ChatEffect

    data object ScrollToBottom : ChatEffect
}

class ChatStore(
    private val chatRepository: ChatRepository
) : Store<ChatState, ChatAction, ChatEffect>(
    ChatState(
        isGenerating = false,
        messages = emptyList(),
        isLoading = false,
        isGenQueriesEnabled = true,
        recommendedQueries = emptyList(),
        isLoadingQueries = true,
        medicalServices = listOf(
            MedicalService(
                id = "1",
                name = "Khám Tổng Quát",
                currentPrice = 500000,
                originalPrice = 600000,
                clinicId = "clinic_1"
            ),
            MedicalService(
                id = "2", 
                name = "Khám Tim Mạch",
                currentPrice = 800000,
                originalPrice = 0,
                clinicId = "clinic_2"
            ),
            MedicalService(
                id = "3",
                name = "Khám Da Liễu", 
                currentPrice = 700000,
                originalPrice = 850000,
                clinicId = "clinic_3"
            ),
            MedicalService(
                id = "4",
                name = "Khám Mắt",
                currentPrice = 600000,
                originalPrice = 0,
                clinicId = "clinic_4"
            ),
            MedicalService(
                id = "5",
                name = "Khám Răng Hàm Mặt",
                currentPrice = 400000,
                originalPrice = 500000,
                clinicId = "clinic_5"
            )
        )
    )
) {
    override val onException: (Throwable) -> Unit
        get() = {
            sendAction(
                ChatAction.Error(it)
            )
        }

    override fun dispatch(
        oldState: ChatState,
        action: ChatAction
    ) {
        when (action) {
            is ChatAction.SendMessage -> {
                if (action.conversationId == null) {
                    return
                }

                if (!oldState.isLoading && !oldState.isGenerating) {
                    val message =
                        generateHumanMessage(action.text, conversationId = action.conversationId)

                    setState(
                        oldState.copy(
                            messages = oldState.messages + message,
                            isGenerating = true
                        )
                    )

                    sendMessage(action.text, conversationId = action.conversationId)
                }
            }

            is ChatAction.GetMessageHistory -> {
                if (!oldState.isLoading) {
                    setState(
                        oldState.copy(isLoading = true, messages = emptyList())
                    )
                }
                getMessageHistory(conversationId = action.conversationId)
            }

            is ChatAction.GetMessageHistorySuccess -> {
                setState(
                    oldState.copy(
                        isLoading = false,
                        messages = action.messages,
                        isGenerating = false,
                        currentConversationId = action.conversationId
                    )
                )
                setEffect(ChatEffect.ScrollToBottom)

                if (action.messages.isNotEmpty() && oldState.isGenQueriesEnabled) {
                    sendAction(ChatAction.GetRecommendedQueries(action.conversationId))
                }
            }

            is ChatAction.SendMessageSuccess -> {
                val messages = oldState.messages + action.message
                setEffect(ChatEffect.ScrollToBottom)
                setState(
                    oldState.copy(
                        messages = messages,
                        isLoading = false,
                        isGenerating = false
                    )
                )

                oldState.currentConversationId?.let { conversationId ->
                    if (oldState.isGenQueriesEnabled) {
                        sendAction(ChatAction.GetRecommendedQueries(conversationId))
                    }
                }
            }

            is ChatAction.Error -> {
                setState(
                    oldState.copy(
                        isLoading = false,
                        isGenerating = false,
                        error = action.error
                    )
                )
            }

            is ChatAction.GetConversationList -> {
                getConversationList(action.showLatest)
            }

            is ChatAction.GetConversationListSuccess -> {
                setState(
                    oldState.copy(
                        isLoading = false,
                        isGenerating = false,
                        conversationList = action.conversations
                    )
                )
                if (action.showLatest) {
                    sendAction(ChatAction.SelectConversation(action.conversations.first().id))
                }
            }

            is ChatAction.NewConversation -> {
                if (!oldState.isLoading) {
                    setState(oldState.copy(isLoading = true))
                }
                createConversation()
            }

            is ChatAction.NewConversationSuccess -> {
                val conversations = oldState.conversationList + action.conversation
                val sorted = conversations.sortedByDescending { it.updatedAt }
                setState(
                    oldState.copy(
                        isLoading = false,
                        messages = emptyList(),
                        currentConversationId = action.conversation.id,
                        conversationList = sorted
                    )
                )
                sendAction(ChatAction.SelectConversation(action.conversation.id))
            }

            is ChatAction.SelectConversation -> {
                if (!oldState.isLoading) {
                    setState(oldState.copy(isLoading = true))
                    getMessageHistory(action.conversationId)
                }
            }

            ChatAction.ClearError -> setState(oldState.copy(error = null))
            ChatAction.GetConversationListError -> {
                setState(
                    oldState.copy(
                        isLoading = false,
                        error = null,
                        conversationList = emptyList()
                    )
                )
            }

            is ChatAction.DeleteConversation -> {
                deleteConversation(action.conversationId)
            }

            is ChatAction.DeleteConversationSuccess -> {
                val conversations = oldState.conversationList.filter {
                    it.id != action.conversationId
                }.sortedByDescending { it.updatedAt }

                setState(
                    oldState.copy(
                        isLoading = false,
                        conversationList = conversations
                    )
                )
            }

            is ChatAction.ToggleGenQueries -> {
                Napier.d { "ToggleGenQueries: ${action.enabled}" }
                setState(oldState.copy(isGenQueriesEnabled = action.enabled))
                if (action.enabled && oldState.currentConversationId != null) {
                    sendAction(
                        ChatAction.GetRecommendedQueries(
                            conversationId = oldState.currentConversationId
                        )
                    )
                }
            }

            is ChatAction.GetRecommendedQueries -> {
                getRecommendedQueries(conversationId = action.conversationId)
            }

            is ChatAction.GetRecommendedQueriesSuccess -> {
                setState(oldState.copy(recommendedQueries = action.queries))
                setEffect(ChatEffect.ScrollToBottom)
            }

            is ChatAction.SelectMedicalService -> {
                setState(oldState.copy(
                    selectedMedicalService = action.service,
                    showMedicalServiceBottomSheet = true
                ))
                sendAction(ChatAction.LoadMedicalServiceClinic(action.service.clinicId))
            }

            ChatAction.HideMedicalServiceBottomSheet -> {
                setState(oldState.copy(
                    selectedMedicalService = null, 
                    showMedicalServiceBottomSheet = false,
                    medicalServiceClinic = null
                ))
            }

            is ChatAction.LoadMedicalServiceClinic -> {
                loadMedicalServiceClinic(action.clinicId)
            }

            is ChatAction.LoadMedicalServiceClinicSuccess -> {
                setState(oldState.copy(medicalServiceClinic = action.clinic))
            }
        }
    }

    private fun deleteConversation(
        conversationId: String
    ) {
        runFlow(
            exception = coroutineExceptionHandler {
            }
        ) {
            chatRepository.deleteConversation(conversationId).collect { success ->
                if (success) {
                    sendAction(ChatAction.DeleteConversationSuccess(conversationId))
                }
            }
        }
    }

    private fun generateHumanMessage(
        text: String,
        conversationId: String
    ): Message {
        val nowDate = nowDateTime()
        return Message(
            id = "$nowDate",
            userId = "",
            content = text,
            conversationId = conversationId,
            isHuman = true,
            timestamp = nowDate
        )
    }

    private fun createConversation() {
        runFlow {
            chatRepository.createConversation().collect { conversation ->
                sendAction(ChatAction.NewConversationSuccess(conversation))
            }
        }
    }

    private fun getConversationList(showLatest: Boolean) {
        runFlow(
            exception = coroutineExceptionHandler {
                sendAction(ChatAction.GetConversationListError)
            }
        ) {
            chatRepository.getConversationList().collect { conversations ->
                val sorted = conversations.sortedByDescending { it.updatedAt }
                sendAction(
                    ChatAction.GetConversationListSuccess(sorted, showLatest)
                )
            }
        }
    }

    private fun getMessageHistory(conversationId: String) {
        runFlow {
            chatRepository.getMessageHistory(conversationId).collect { messages ->
                sendAction(ChatAction.GetMessageHistorySuccess(messages, conversationId))
            }
        }
    }

    private fun sendMessage(
        text: String,
        conversationId: String
    ) {
        runFlow {
            chatRepository.sendMessage(message = text, conversationId = conversationId)
                .collect { message ->
                    sendAction(ChatAction.SendMessageSuccess(message, conversationId))
                }
        }
    }

    private fun getRecommendedQueries(conversationId: String) {
        runFlow(
            exception = coroutineExceptionHandler {
            }
        ) {
            chatRepository.getRecommendAiQuery(conversationId).collect {
                sendAction(ChatAction.GetRecommendedQueriesSuccess(it))
            }
        }
    }

    private fun loadMedicalServiceClinic(clinicId: String) {
        // Mock clinic data for now since we don't have the API
        val mockClinic = when (clinicId) {
            "clinic_1" -> Clinic(
                id = "clinic_1",
                name = "Bệnh viện Đa khoa Hồng Ngọc",
                address = "55 Yên Ninh, Quán Thánh, Ba Đình, Hà Nội",
                logo = "https://example.com/logo1.png",
                hotline = "024 3927 5568"
            )
            "clinic_2" -> Clinic(
                id = "clinic_2", 
                name = "Bệnh viện Tim Hà Nội",
                address = "92 Trần Hưng Đạo, Hoàn Kiếm, Hà Nội",
                logo = "https://example.com/logo2.png", 
                hotline = "024 3942 6969"
            )
            "clinic_3" -> Clinic(
                id = "clinic_3",
                name = "Bệnh viện Da liễu Hà Nội",
                address = "15 Phùng Khoang, Trung Văn, Nam Từ Liêm, Hà Nội",
                logo = "https://example.com/logo3.png",
                hotline = "024 3556 7890"
            )
            "clinic_4" -> Clinic(
                id = "clinic_4",
                name = "Bệnh viện Mắt Hà Nội", 
                address = "85 Bà Triệu, Hai Bà Trưng, Hà Nội",
                logo = "https://example.com/logo4.png",
                hotline = "024 3821 9191"
            )
            "clinic_5" -> Clinic(
                id = "clinic_5",
                name = "Bệnh viện Răng Hàm Mặt Trung ương Hà Nội",
                address = "40A Tràng Thi, Hoàn Kiếm, Hà Nội", 
                logo = "https://example.com/logo5.png",
                hotline = "024 3825 4011"
            )
            else -> Clinic(
                id = clinicId,
                name = "Phòng khám Y tế", 
                address = "Hà Nội",
                logo = "https://example.com/default_logo.png",
                hotline = "024 3xxx xxxx"
            )
        }
        
        sendAction(ChatAction.LoadMedicalServiceClinicSuccess(mockClinic))
    }
}
