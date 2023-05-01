package jp.mowaro.mogra

import android.app.Activity
import android.app.AlertDialog
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import jp.mowaro.mogra.databinding.FragmentFirstBinding
import jp.mowaro.mogra.dialog.picker.directory.DirectoryPicker
import jp.mowaro.mogra.dialog.picker.directory.OnSelectListener
import jp.mowaro.mogra.util.PermissionGetter
import jp.mowaro.mogra.util.Setting
import java.io.File


/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment(), OnSelectListener {
    private var _binding: FragmentFirstBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {

        _binding = FragmentFirstBinding.inflate(inflater, container, false)

        initSpinner(binding.spnOrderBy, R.array.order_by)
        initSpinner(binding.spnOrient, R.array.auto_orient)

        readPreference()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as MainActivity).showSupportActionBar()

        binding.btnStart.setOnClickListener {
            val activity: Activity = context as Activity
            if (PermissionGetter().isAllowedManageExternalStorage(activity)) {
                findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
            } else {
                AlertDialog.Builder(context)
                    .setMessage("権限がないよ")
                    .setPositiveButton("OK") { _, _ -> return@setPositiveButton }
                    .create()
            }
        }
        binding.btnDirectory.setOnClickListener {
            val activity: Activity = context as Activity
            if (PermissionGetter().isAllowedManageExternalStorage(activity)) {
                setTargetDirectory()
            } else {
                AlertDialog.Builder(context)
                    .setMessage("権限がないよ")
                    .setPositiveButton("OK") { _, _ -> return@setPositiveButton }
                    .show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setTargetDirectory() {
        val dialog = DirectoryPicker(requireContext(), this)
        var file = File(binding.txtDirectory.text.toString())
        if (!file.exists()) file = File(Environment.getExternalStorageDirectory().path)
        dialog.show(file)
    }

    /**
     * スピナ初期設定
     *
     * 参考：
     * https://codeforfun.jp/android-studio-how-to-customize-spinner/
     * https://akira-watson.com/android/kotlin/spinner-simple.html
     *
     * @param spinner 設定するスピナ
     * @param itemsArray スピナに設定するアイテムのResourceId
     */
    private fun initSpinner(spinner: Spinner, itemsArray: Int) {
        val adapter: ArrayAdapter<String> = ArrayAdapter(requireContext(),R.layout.custom_spinner, resources.getStringArray(itemsArray))
        adapter.setDropDownViewResource(R.layout.custom_spinner_dropdown)
        spinner.adapter = adapter

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                writePreference()
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                writePreference()
            }
        }
    }

    /**
     * 設定書き込み
     */
    private fun writePreference() {
        Setting.orderBy = binding.spnOrderBy.selectedItem.toString()
        Setting.orient = binding.spnOrient.selectedItem.toString()
        Setting.directory = binding.txtDirectory.text.toString()
        Setting.commit()
    }

    /**
     * 設定読み込み
     */
    private fun readPreference() {
        setSelectionByText(binding.spnOrderBy, Setting.orderBy)
        setSelectionByText(binding.spnOrient, Setting.orient)
        binding.txtDirectory.text = Setting.directory
    }

    /**
     * 項目名でスピナを選択する
     * 参考：https://qiita.com/shinmai333/items/9d02f72c89652af9c4b0
     */
    private fun setSelectionByText(spinner: Spinner, itemText: String?) {
        for (i in 0 until spinner.adapter.count) {
            if (spinner.adapter.getItem(i) == itemText) {
                spinner.setSelection(i)
                return
            }
        }
    }

    override fun onSelect(file: File) {
        binding.txtDirectory.text = file.absolutePath
        writePreference()
    }
}