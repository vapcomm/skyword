package online.vapcom.skyword.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import online.vapcom.skyword.R

private const val TAG = "MainActivi"

class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.i(TAG, "---- CREATE")

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // навигационный контроллер, который в контейнере nav_fragment отображает фрагменты из графа навигации
        navController = Navigation.findNavController(this, R.id.nav_fragment)

    }

    // отдаёт обработку кнопки Up навигационному контроллеру
    override fun onSupportNavigateUp(): Boolean {
        return NavigationUI.navigateUp(navController, null)
    }

    private fun getCurrentFragment() : Fragment? {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_fragment)
        return navHostFragment?.childFragmentManager?.fragments?.getOrNull(0)
    }

    override fun onBackPressed() {
        Log.i(TAG, "---- ON BACK PRESSED")

        val currentFragment = getCurrentFragment()
        if (currentFragment != null && currentFragment is BaseFragment && currentFragment.onBackPressed()) {
            return
        }
        super.onBackPressed()
    }

}