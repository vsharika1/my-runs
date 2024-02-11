package com.example.vishavjit_harika

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat

/**
 * Resources used:
 * 1. For listView and its sub-item: https://stackoverflow.com/questions/7916834/adding-listview-sub-item-text-in-android
 * 2. For listView and its sub-item: https://www.geeksforgeeks.org/simpleadapter-in-android-with-example/
 * 3. For fixing issue with Fragment Manager: https://stackoverflow.com/questions/60830741/kotlin-fragmentmanager-is-showing-error-when-called-from-fragment-working-with
 * 4. Open URL on the click of a button: https://stackoverflow.com/questions/4930228/open-a-url-on-click-of-ok-button-in-android
 * 5. List preferences: https://stackoverflow.com/questions/9880841/using-list-preference-in-android
 */

class FragmentSettings : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.preferences)
    }
}

// Implementation using ListView:
//
//class FragmentSettings : Fragment() {
//    private val accountPreferencesArray = arrayOf("Name, Email, Class, etc", "Privacy Setting")
//    private val apSubTextArray = arrayOf("User Profile", "Posting your records anonymously")
//    private val additionalSettingsArray = arrayOf("Unit Preference", "Comments")
//    private val asSubTextArray = arrayOf("Select the units", "Please enter your comments")
//    private val miscArray = arrayOf("Webpage")
//    private val miscSubTextArray = arrayOf("https://www.sfu.ca/computing.html")
//
//    private val listTitle = arrayOf("Setting", "Description")
//
//    private lateinit var accountPreferencesListView: ListView
//    private lateinit var additionalSettingsListView: ListView
//    private lateinit var miscListView: ListView
//
//    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
//                              savedInstanceState: Bundle?): View? {
//        val inf = inflater.inflate(R.layout.fragment_settings, container, false)
//
//        accountPreferencesListView = inf.findViewById(R.id.account_preferences_listView)
//        additionalSettingsListView = inf.findViewById(R.id.additional_settings_Listview)
//        miscListView = inf.findViewById(R.id.misc_listView)
//
//        val apList = ArrayList<HashMap<String,String>>()
//        for (i in accountPreferencesArray.indices) {
//            val apMap = HashMap<String,String>()
//
//            apMap["Setting"] = accountPreferencesArray[i]
//            apMap["Description"] = apSubTextArray[i]
//
//            apList.add(apMap)
//        }
//
//        val apSimpleAdapter: SimpleAdapter = SimpleAdapter(
//            inf.context, apList, android.R.layout.simple_list_item_2, listTitle, intArrayOf(android.R.id.text1, android.R.id.text2)
//        )
//        accountPreferencesListView.adapter = apSimpleAdapter
//
//        accountPreferencesListView.setOnItemClickListener() { parent: AdapterView<*>, view: View, position: Int, id: Long ->
//            when(position) {
//                0 -> {
//                    val intent: Intent = Intent(inf.context, ProfileSettingsActivity::class.java)
//                    startActivity(intent)
//                }
//            }
//        }
//
//        val asList = ArrayList<HashMap<String,String>>()
//        for (i in additionalSettingsArray.indices) {
//            val asMap = HashMap<String,String>()
//
//            asMap["Setting"] = additionalSettingsArray[i]
//            asMap["Description"] = asSubTextArray[i]
//
//            asList.add(asMap)
//        }
//
//        val asSimpleAdapter: SimpleAdapter = SimpleAdapter(
//            inf.context, asList, android.R.layout.simple_list_item_2, listTitle, intArrayOf(android.R.id.text1, android.R.id.text2)
//        )
//        additionalSettingsListView.adapter = asSimpleAdapter
//
//        additionalSettingsListView.setOnItemClickListener() { parent: AdapterView<*>, view: View, position: Int, id: Long ->
//            when(position) {
//                0 -> {
//                    val myDialog = MyDialog()
//                    val bundle = Bundle()
//                    bundle.putInt(MyDialog.DIALOG_KEY, MyDialog.UNIT_PREFERENCE_DIALOG)
//                    myDialog.arguments = bundle
//                    myDialog.show(childFragmentManager, "unit preference dialog")
//                }
//                1 -> {
//                    val myDialog = MyDialog()
//                    val bundle = Bundle()
//                    bundle.putInt(MyDialog.DIALOG_KEY, MyDialog.COMMENTS_SETTINGS_DIALOG)
//                    myDialog.arguments = bundle
//                    myDialog.show(childFragmentManager, "comments settings dialog")
//                }
//            }
//        }
//
//        val miscList = ArrayList<HashMap<String,String>>()
//        for (i in miscArray.indices) {
//            val miscMap = HashMap<String,String>()
//
//            miscMap["Setting"] = miscArray[i]
//            miscMap["Description"] = miscSubTextArray[i]
//
//            miscList.add(miscMap)
//        }
//
//        val miscSimpleAdapter: SimpleAdapter = SimpleAdapter(
//            inf.context, miscList, android.R.layout.simple_list_item_2, listTitle, intArrayOf(android.R.id.text1, android.R.id.text2)
//        )
//        miscListView.adapter = miscSimpleAdapter
//
//        miscListView.setOnItemClickListener() { parent: AdapterView<*>, view: View, position: Int, id: Long ->
//            when(position) {
//                0 -> {
//                    val uri: Uri? = Uri.parse("https://www.sfu.ca/computing.html")
//                    val intent = Intent(Intent.ACTION_VIEW, uri)
//                    startActivity(intent)
//                }
//            }
//        }
//
//        return inf
//    }
//}
