package jp.mowaro.mogra

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import jp.mowaro.mogra.util.Setting


class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        try {
            super.onCreate(savedInstanceState)
            Setting.initialize(this)
            setContentView(R.layout.activity_main)

            initializeActionBar()

        } catch (e: java.lang.Exception) {
            AlertDialog.Builder(this)
                .setTitle("Exception")
                .setMessage(e.message)
                //.setPositiveButton("ok") {} // なにもしない
                .show()
        }
    }

    /**
     * ActionBarを初期化する
     *
     * toolbarを設定せずに、単に"supportActionBar()?.hide"とするサンプルは多いが、これではsupportActionBarがnullを返しうまくいかなかった
     * 参考：https://note.com/nttrtech/n/n9e597461eec9
     */
    private fun initializeActionBar() {
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
    }

    /**
     * ActionBarを隠す
     */
    fun hideSupportActionBar() {
        supportActionBar?.hide()
    }

    /**
     * ActionBarを表示する
     */
    fun showSupportActionBar() {
        supportActionBar?.show()
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
}