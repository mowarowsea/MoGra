package jp.mowaro.mogra

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Size
import android.view.*
import androidx.fragment.app.Fragment
import android.view.View.OnTouchListener
import android.webkit.MimeTypeMap
import androidx.core.net.toUri
import jp.mowaro.mogra.databinding.FragmentSecondBinding
import jp.mowaro.mogra.file.FileProvider
import jp.mowaro.mogra.image.ImageProvider
import java.io.File

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class SecondFragment : Fragment(), OnTouchListener {
    private lateinit var fileProvider: FileProvider
    private lateinit var imageProvider: ImageProvider
    private var _binding: FragmentSecondBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    /**
     * https://android-note.open-memo.net/sub/other_view__get_view_size.html
     */
    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSecondBinding.inflate(inflater, container, false)
        (activity as MainActivity).hideSupportActionBar()

        this.fileProvider = FileProvider(this.requireContext())
        createImageView()

        return binding.root
    }

    private fun createImageView() {
        val dm = DisplayMetrics()
        this.requireActivity().windowManager.defaultDisplay.getMetrics(dm)
        val size = Size(dm.widthPixels, dm.heightPixels)
        this.imageProvider = ImageProvider(size, this.requireContext())

        reload()
    }

    private fun reload() {
        var file = fileProvider.open()
        while (!isImage(file)) {
            file = fileProvider.getNext()
        }
        binding.imageView.setImageDrawable(imageProvider.get(file.absolutePath))
    }
    private fun next() {
        var file = fileProvider.getNext()
        while (!isImage(file)) {
            file = fileProvider.getNext()
        }
        binding.imageView.setImageDrawable(imageProvider.get(file.absolutePath))
    }
    private fun prev() {
        var file = fileProvider.getPrev()
        while (!isImage(file)) {
            file = fileProvider.getPrev()
        }
        binding.imageView.setImageDrawable(imageProvider.get(file.absolutePath))
    }

    private fun isImage(file: File): Boolean {
        val result: String? = getMimeType(requireContext(), file.toUri())
        return result?.startsWith("image") ?: false
    }

    /**
     * https://akangcupez.com/snippets/android-file-mime-type-kotlin/
     */
    private fun getMimeType(context: Context, uri: Uri): String? {
        val mimeType: String? = if (ContentResolver.SCHEME_CONTENT == uri.scheme) {
            val cr: ContentResolver = context.contentResolver
            cr.getType(uri)
        } else {
            val fileExtension = MimeTypeMap.getFileExtensionFromUrl(uri.toString())
            MimeTypeMap.getSingleton().getMimeTypeFromExtension(fileExtension.lowercase())
        }
        return mimeType
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.setOnTouchListener(this)
    }

    override fun onTouch(view: View?, motionEvent: MotionEvent?): Boolean {
        if (view == null || motionEvent == null) return false
        if (motionEvent.action != MotionEvent.ACTION_DOWN) return false //gestureDetector.onFling(motionEvent.)

        view.performClick()
        val width = view.width
        val prevRange = 0F..(width / 3F)
        val nextRange = (width * (2 / 3F))..width.toFloat()
        when(motionEvent.x) {
            in prevRange -> prev()
            in nextRange -> next()
        }
        return true
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}