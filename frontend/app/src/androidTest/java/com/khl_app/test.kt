package com.khl_app
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.isDisplayed
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.onNodeWithContentDescription
import com.khl_app.ui.MainActivity
import org.junit.FixMethodOrder
import org.junit.runners.MethodSorters

@RunWith(AndroidJUnit4::class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class IntegrationsTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun test1_Auth() {
        composeTestRule.waitUntil(15000) {
            try {
                composeTestRule.onNodeWithText("Логин").isDisplayed()
            } catch (e: Exception) {
                false
            }
        }

        composeTestRule.onNodeWithText("Логин").performTextInput("nick")
        composeTestRule.onNodeWithText("Пароль").performTextInput("13691303")

        composeTestRule.onNodeWithText("Войти").performClick()

        composeTestRule.waitUntil(15000) {
            try {
                composeTestRule.onNodeWithText("Календарь").isDisplayed()
            } catch (e: Exception) {
                false
            }
        }

        composeTestRule.onNodeWithText("Календарь").assertIsDisplayed()
    }

    @Test
    fun test2_ViewProfile() {
        composeTestRule.waitUntil(15000) {
            try {
                composeTestRule.onNodeWithText("Календарь").isDisplayed()
            } catch (e: Exception) {
                false
            }
        }

        composeTestRule.onNodeWithContentDescription("Menu Button").performClick()

        composeTestRule.waitUntil(5000) {
            try {
                composeTestRule.onNodeWithText("Профиль").isDisplayed()
            } catch (e: Exception) {
                false
            }
        }

        composeTestRule.onNodeWithText("Профиль").performClick()

        composeTestRule.waitUntil(15000) {
            try {
                composeTestRule.onNodeWithText("nick").isDisplayed()
            } catch (e: Exception) {
                false
            }
        }

        composeTestRule.onNodeWithText("nick").assertIsDisplayed()

        composeTestRule.waitUntil(10000) {
            try {
                composeTestRule.onNodeWithText("Баллов получено").isDisplayed() &&
                        composeTestRule.onNodeWithText("Предсказано игр").isDisplayed()
            } catch (e: Exception) {
                false
            }
        }

        composeTestRule.waitUntil(10000) {true}
    }

    @Test
    fun test3_FilterAndPlaceBet() {
        composeTestRule.waitUntil(15000) {
            try {
                composeTestRule.onNodeWithText("Календарь").isDisplayed()
            } catch (e: Exception) {
                false
            }
        }

        if (!composeTestRule.onNodeWithText("Календарь").isDisplayed()) {
            composeTestRule.onNodeWithContentDescription("Меню").performClick()

            if (!composeTestRule.onNodeWithContentDescription("Меню").isDisplayed()) {
                composeTestRule.onNodeWithContentDescription("Menu").performClick()
            }

            composeTestRule.waitUntil(5000) {
                try {
                    composeTestRule.onNodeWithText("Календарь").isDisplayed()
                } catch (e: Exception) {
                    false
                }
            }

            composeTestRule.onNodeWithText("Календарь").performClick()

            composeTestRule.waitUntil(10000) {
                try {
                    composeTestRule.onNodeWithText("Календарь").isDisplayed()
                } catch (e: Exception) {
                    false
                }
            }
        }

        composeTestRule.onNodeWithContentDescription("Настройки и фильтры").performClick()

        composeTestRule.waitUntil(5000) {
            try {
                composeTestRule.onNodeWithText("Поиск команд").isDisplayed()
            } catch (e: Exception) {
                false
            }
        }

        composeTestRule.onNodeWithText("Поиск команд").performTextInput("Салават Юлаев")

        composeTestRule.waitUntil(5000) {
            try {
                composeTestRule.onNode(hasTestTag("teamItem") and hasText("Салават Юлаев")).isDisplayed()
            } catch (e: Exception) {
                false
            }
        }

        composeTestRule.onNode(hasTestTag("teamItem") and hasText("Салават Юлаев")).performClick()

        composeTestRule.onNodeWithContentDescription("Применить").performClick()

        composeTestRule.waitUntil(10000) {
            try {
                composeTestRule.onNodeWithText("Календарь").isDisplayed()
            } catch (e: Exception) {
                false
            }
        }

        composeTestRule.waitUntil(10000) {
            try {
                composeTestRule.onAllNodesWithText("Салават Юлаев").fetchSemanticsNodes().isNotEmpty()
            } catch (e: Exception) {
                false
            }
        }

        composeTestRule.onAllNodesWithText("Салават Юлаев")
            .onFirst()
            .performClick()

        composeTestRule.waitUntil(5000) {
            try {
                composeTestRule.onNode(hasTestTag("scoreInput_Сибирь")).isDisplayed() &&
                        composeTestRule.onNode(hasTestTag("scoreInput_Салават Юлаев")).isDisplayed()
            } catch (e: Exception) {
                false
            }
        }

        composeTestRule.onNode(hasTestTag("scoreInput_Сибирь")).performTextInput("1")
        composeTestRule.onNode(hasTestTag("scoreInput_Салават Юлаев")).performTextInput("3")

        composeTestRule.onNodeWithText("Сохранить").performClick()

        composeTestRule.waitUntil(10000) {
            try {
                composeTestRule.onNodeWithText("Календарь").isDisplayed()
            } catch (e: Exception) {
                false
            }
        }

        composeTestRule.waitUntil(1000) {true}

        composeTestRule.onNodeWithText("Календарь").assertIsDisplayed()
    }
}