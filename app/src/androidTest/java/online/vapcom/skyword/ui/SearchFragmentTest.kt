/*
 * (c) VAP Communications Group, 2020
 */

package online.vapcom.skyword.ui

import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import online.vapcom.skyword.R
import online.vapcom.skyword.ServiceLocator
import online.vapcom.skyword.data.DictRepositoryFake
import org.hamcrest.core.IsNot
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@MediumTest
@RunWith(AndroidJUnit4::class)
class SearchFragmentTest {

    private lateinit var repository: DictRepositoryFake

    @Before
    fun setUp() {
        repository = DictRepositoryFake()
        ServiceLocator.dictRepository = repository
    }

    @After
    fun cleanUp() = runBlockingTest {
        ServiceLocator.reset()
    }

    /**
     * Проверка отображения всех виджетов в исходном состоянии
     */
    @Test
    fun displayedAllUI() = runBlockingTest{

        launchFragmentInContainer<SearchFragment>(themeResId = R.style.AppTheme)

        onView(withId(R.id.appbar)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        onView(withId(R.id.toolbar)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        onView(withId(R.id.word)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        onView(withId(R.id.word_text)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        onView(withId(R.id.word_text)).check(ViewAssertions.matches(ViewMatchers.withText("")))

        onView(withId(R.id.search)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        onView(withId(R.id.search)).check(ViewAssertions.matches(IsNot.not(ViewMatchers.isEnabled()))) // при пустой строке кнопка блокирована

        onView(withId(R.id.content)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        onView(withId(R.id.found_words)).check(ViewAssertions.matches(ViewMatchers.hasChildCount(0)))   // список найденных пустой

        onView(withId(R.id.progress)).check(ViewAssertions.matches(IsNot.not(ViewMatchers.isDisplayed())))
        onView(withId(R.id.fatal_error)).check(ViewAssertions.matches(IsNot.not(ViewMatchers.isDisplayed())))
    }


    /**
     * Проверяем состояние списка найденных слов после успешного поиска
     */
    @Test
    fun foundWords() {
        launchFragmentInContainer<SearchFragment>(themeResId = R.style.AppTheme)
        onView(withId(R.id.word_text)).perform(ViewActions.typeText("rain"))

        // кликаем кнопку поиска
        onView(withId(R.id.search)).perform(ViewActions.click())
        // в списке найденных две записи
        onView(withId(R.id.found_words)).check(ViewAssertions.matches(ViewMatchers.hasChildCount(2)))
    }
}