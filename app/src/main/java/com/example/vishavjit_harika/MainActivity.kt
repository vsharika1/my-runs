package com.example.vishavjit_harika

/**
 * Resources used to complete MyRuns2:
 * 1. For listView and its sub-item: https://stackoverflow.com/questions/7916834/adding-listview-sub-item-text-in-android
 * 2. For listView and its sub-item: https://www.geeksforgeeks.org/simpleadapter-in-android-with-example/
 * 3. For fixing issue with Fragment Manager: https://stackoverflow.com/questions/60830741/kotlin-fragmentmanager-is-showing-error-when-called-from-fragment-working-with
 * 4. Open URL on the click of a button: https://stackoverflow.com/questions/4930228/open-a-url-on-click-of-ok-button-in-android
 * 5. For choosing photo from gallery: https://www.youtube.com/watch?v=poAUbNY2dEs
 * 6. For drop down selector: https://stackoverflow.com/questions/55684917/the-spinner-doesnt-work-in-my-kotlin-fragment
 * 7. For drop down selector: https://developer.android.com/develop/ui/views/components/spinner
 * 8. LayoutKotlin Demo provided by professor: https://canvas.sfu.ca/courses/80625/files/22294116?wrap=1
 * 9. DialogFragmentKotlin Demo provided by professor: https://canvas.sfu.ca/courses/80625/files/22345741?wrap=1
 * 10. ActionTabsKotlin Demo provided by professor: https://canvas.sfu.ca/courses/80625/files/22294112?wrap=1
 * 11. Setting intent function within .xml: https://developer.android.com/guide/components/intents-filters
 * 12. List preferences: https://stackoverflow.com/questions/9880841/using-list-preference-in-android
 * 13. For basic queries and debugging: ChatGPT (Version 3.5) {https://chat.openai.com/auth/login}
 */

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class MainActivity : AppCompatActivity() {
    private lateinit var fragmentStart: FragmentStart
    private lateinit var fragmentHistory: FragmentHistory
    private lateinit var fragmentSettings: FragmentSettings
    private lateinit var viewPager2: ViewPager2
    private lateinit var tabLayout: TabLayout
    private lateinit var myMyFragmentStateAdapter: MyFragmentStateAdapter
    private lateinit var fragments: ArrayList<Fragment>
    private val tabTitles = arrayOf("START", "HISTORY", "SETTINGS") //Tab titles
    private lateinit var tabConfigurationStrategy: TabLayoutMediator.TabConfigurationStrategy
    private lateinit var tabLayoutMediator: TabLayoutMediator

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewPager2 = findViewById(R.id.viewpager)
        tabLayout = findViewById(R.id.tabContainer)

        fragmentStart = FragmentStart()
        fragmentHistory = FragmentHistory()
        fragmentSettings = FragmentSettings()

        fragments = java.util.ArrayList()
        fragments.add(fragmentStart)
        fragments.add(fragmentHistory)
        fragments.add(fragmentSettings)

        myMyFragmentStateAdapter = MyFragmentStateAdapter(this, fragments)
        viewPager2.adapter = myMyFragmentStateAdapter

        tabConfigurationStrategy =
            TabLayoutMediator.TabConfigurationStrategy { tab: TabLayout.Tab, position: Int ->
                tab.text = tabTitles[position]
            }
        tabLayoutMediator = TabLayoutMediator(tabLayout, viewPager2, tabConfigurationStrategy)
        tabLayoutMediator.attach()
    }

    override fun onDestroy() {
        super.onDestroy()
        tabLayoutMediator.detach()
    }

    fun openManualEntryActivity(v: View?) {
        val intent = Intent(this, ManualEntryActivity::class.java)
        startActivity(intent)
    }
}