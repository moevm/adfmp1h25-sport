package com.khl_app
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.testing.TestNavHostController
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.filterToOne
import androidx.compose.ui.test.hasAnyDescendant
import androidx.compose.ui.test.hasParent
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.isDisplayed
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import com.khl_app.ui.MainActivity
import com.khl_app.ui.navigation.AppNavGraph


@RunWith(AndroidJUnit4::class)
class IntegrationsTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun testAuthAndViewProfileAndFilter() {
        composeTestRule.waitForIdle()
        runBlocking {
            delay(4000)
        }

        // 2. Вводим данные для авторизации
        composeTestRule.onNodeWithText("Логин").assertIsDisplayed()
        composeTestRule.onNodeWithText("Логин").performTextInput("nick")
        composeTestRule.onNodeWithText("Пароль").performTextInput("13691303")

        // 3. Нажимаем кнопку входа и ждем перехода на главный экран
        composeTestRule.onNodeWithText("Войти").performClick()
        composeTestRule.waitForIdle()

        runBlocking {
            delay(7000)
        }

        composeTestRule.onNodeWithText("Календарь").assertIsDisplayed()

        composeTestRule.onNodeWithContentDescription("Menu Button").performClick()
        composeTestRule.waitForIdle()

        runBlocking {
            delay(2000)
        }

        composeTestRule.onNodeWithText("Профиль").assertIsDisplayed()
        composeTestRule.onNodeWithText("Профиль").performClick()

        runBlocking {
            delay(10000)
        }
        composeTestRule.waitForIdle()

        composeTestRule.waitUntil(15000) {
            try {
                composeTestRule.onNodeWithText("nick").isDisplayed()
            } catch (e: Exception) {
                false
            }
        }

        composeTestRule.onNodeWithText("nick").assertIsDisplayed()

        // Проверяем наличие других элементов профиля,
        // которые должны появиться после полной загрузки данных
        try {
            composeTestRule.onNodeWithText("Баллов получено").assertIsDisplayed()
            composeTestRule.onNodeWithText("Предсказано игр").assertIsDisplayed()
        } catch (e: Exception) {
            runBlocking { delay(5000) }
            composeTestRule.waitForIdle()
            composeTestRule.onNodeWithText("Баллов получено").assertIsDisplayed()
            composeTestRule.onNodeWithText("Предсказано игр").assertIsDisplayed()
        }

        composeTestRule.onNodeWithContentDescription("Меню")
            .performClick() // или ищите по тегу, если contentDescription отличается

        if (!composeTestRule.onNodeWithContentDescription("Меню").isDisplayed()) {
            composeTestRule.onNodeWithContentDescription("Menu").performClick()
        }

        composeTestRule.waitForIdle()

        runBlocking {
            delay(2000)
        }

        composeTestRule.onNodeWithText("Календарь").assertIsDisplayed()
        composeTestRule.onNodeWithText("Календарь").performClick()

        runBlocking {
            delay(5000)
        }

        // 10. Проверяем, что вернулись на экран календаря
        composeTestRule.waitUntil(10000) {
            try {
                composeTestRule.onNodeWithText("Календарь").isDisplayed()
            } catch (e: Exception) {
                false
            }
        }

        // 11. Открываем фильтр (нажимаем на иконку шестеренки)
        composeTestRule.onNodeWithContentDescription("Настройки и фильтры").performClick()

        runBlocking {
            delay(2000)
        }

        // 12. Ищем поле поиска и вводим "Салават Юлаева"
        composeTestRule.onNodeWithText("Поиск команд").performTextInput("Салават Юлаев")

        runBlocking {
            delay(2000)
        }

        composeTestRule.onNode(hasTestTag("teamItem") and hasText("Салават Юлаев")).performClick()

        runBlocking {
            delay(1000)
        }

        // 14. Нажимаем на галочку, чтобы применить фильтр
        composeTestRule.onNodeWithContentDescription("Применить").performClick()

        // 15. Ждем применения фильтра и обновления списка событий
        runBlocking {
            delay(5000)
        }

        // 16. Проверяем, что фильтр применился и мы вернулись на экран календаря
        composeTestRule.onNodeWithText("Календарь").assertIsDisplayed()


        // После проверки, что фильтр применился и мы вернулись на экран календаря
        composeTestRule.onNodeWithText("Календарь").assertIsDisplayed()

        runBlocking {
            delay(2000)  // Даем время для загрузки отфильтрованных матчей
        }

        composeTestRule.onAllNodesWithText("Салават Юлаев")
            .onFirst()
            .performClick()

        runBlocking {
            delay(2000)
        }

        composeTestRule.onNode(hasTestTag("scoreInput_Сибирь")).performTextInput("1")

        composeTestRule.onNode(hasTestTag("scoreInput_Салават Юлаев")).performTextInput("3")

        composeTestRule.onNodeWithText("Сохранить").performClick()

        runBlocking {
            delay(5000)
        }

        composeTestRule.onNodeWithText("Календарь").assertIsDisplayed()
    }
}