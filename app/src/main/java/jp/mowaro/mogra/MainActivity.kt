package jp.mowaro.mogra

import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.appcompat.app.AlertDialog
import jp.mowaro.mogra.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        try {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_main)

            initSpinner(R.id.orderSpinner, R.array.orderItems)
            initSpinner(R.id.orientSpinner, R.array.autoOrientItems)

        } catch (e: java.lang.Exception) {
            AlertDialog.Builder(this)
                .setTitle("Exception")
                .setMessage(e.message)
                .setPositiveButton("ok") { dialog, which ->
                    // 何もしない
                }
                .show()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }

    /**
     * スピナ初期設定
     *
     * 参考：
     * https://codeforfun.jp/android-studio-how-to-customize-spinner/
     * https://akira-watson.com/android/kotlin/spinner-simple.html
     *
     * @param view 設定するスピナのviewId
     * @param itemsArray スピナに設定するアイテムのResourceId
     */
    private fun initSpinner(view: Int, itemsArray: Int) {
        val spinner = findViewById<Spinner>(view)
        val adapter: ArrayAdapter<String> = ArrayAdapter(this,R.layout.custom_spinner, resources.getStringArray(itemsArray))
        adapter.setDropDownViewResource(R.layout.custom_spinner_dropdown)
        spinner.adapter = adapter

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedItem: String = spinner.selectedItem as String
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                val selectedItem: String = spinner.selectedItem as String
            }
        }

    }
}