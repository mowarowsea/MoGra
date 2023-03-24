package jp.mowaro.mogra

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.navigation.fragment.findNavController
import jp.mowaro.mogra.databinding.FragmentFirstBinding

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment() {
    private lateinit var preference: SharedPreferences
    private var preferencesName: String = "settings"

    private var _binding: FragmentFirstBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {

        _binding = FragmentFirstBinding.inflate(inflater, container, false)

        initSpinner(binding.spnOrderBy, R.array.orderItems)
        initSpinner(binding.spnOrient, R.array.autoOrientItems)

        preference = requireContext().getSharedPreferences(preferencesName, Context.MODE_PRIVATE)
        readPreference()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnStart.setOnClickListener {
            findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
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

    private fun writePreference() {
        val edit = preference.edit()
        edit.putInt(R.id.spnOrderBy.toString(), binding.spnOrderBy.selectedItemPosition)
        edit.putInt(R.id.spnOrient.toString(), binding.spnOrient.selectedItemPosition)
        edit.putString(R.id.txtDirectory.toString(), binding.txtDirectory.text.toString())
        edit.apply()
    }
    private fun readPreference() {
        binding.spnOrderBy.setSelection(preference.getInt(R.id.spnOrderBy.toString(), 0))
        binding.spnOrient.setSelection(preference.getInt(R.id.spnOrient.toString(), 0))
        binding.txtDirectory.text = preference.getString(R.id.txtDirectory.toString(), "")
    }

}