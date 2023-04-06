package jp.mowaro.mogra

import android.content.Context
import android.os.Bundle
import android.util.Size
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import jp.mowaro.mogra.databinding.FragmentSecondBinding
import java.io.File

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class SecondFragment : Fragment() {
    private lateinit var setting: Setting
    private lateinit var fileProvider: FileProvider
    private lateinit var imageProvider: ImageProvider
    private var _binding: FragmentSecondBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSecondBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.setting = Setting(context)
        this.setting.directory = "/storage/emulated/0/.エロtmp"
        this.fileProvider = FileProvider(setting)
        val size = Size(binding.imageView.width, binding.imageView.height)
        this.imageProvider = ImageProvider(size, setting)

        val file: File = fileProvider.open()
        binding.imageView.setImageDrawable(imageProvider.get(file.absolutePath))
    }
}