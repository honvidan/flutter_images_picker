package com.imagepicker.adapter

import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.honvidan.flutter_images_picker.R
import com.imagepicker.adapter.ImagePickerAdapter.ImageViewHolder
import com.imagepicker.features.imageloader.ImageLoader
import com.imagepicker.features.imageloader.ImageType
import com.imagepicker.helper.ImagePickerUtils
import com.imagepicker.listeners.OnImageClickListener
import com.imagepicker.listeners.OnImageSelectedListener
import com.imagepicker.model.Image
import kotlinx.android.synthetic.main.ef_imagepicker_item_image.view.*
import java.util.HashMap

class ImagePickerAdapter(
    context: Context,
    imageLoader: ImageLoader,
    selectedImages: List<Image>,
    private val itemClickListener: OnImageClickListener
) : BaseListAdapter<ImageViewHolder>(context, imageLoader) {

    private val images: MutableList<Image> = mutableListOf()
    val selectedImages: MutableList<Image> = mutableListOf()

    private var imageSelectedListener: OnImageSelectedListener? = null
    private val videoDurationHolder = HashMap<Long, String?>()

    init {
        if (selectedImages.isNotEmpty()) {
            this.selectedImages.addAll(selectedImages)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val layout = inflater.inflate(
            R.layout.ef_imagepicker_item_image,
            parent,
            false
        )
        return ImageViewHolder(layout)
    }

    override fun onBindViewHolder(viewHolder: ImageViewHolder, position: Int) {
        val image = images.getOrNull(position) ?: return

        val isSelected = isSelected(image)
        imageLoader.loadImage(image, viewHolder.imageView, ImageType.GALLERY)

        var showFileTypeIndicator = false
        var fileTypeLabel: String? = ""

        if (ImagePickerUtils.isGifFormat(image)) {
            fileTypeLabel = context.resources.getString(R.string.ef_gif)
            showFileTypeIndicator = true
        }

        if (ImagePickerUtils.isVideoFormat(image)) {
            if (!videoDurationHolder.containsKey(image.id)) {
                val uri = Uri.withAppendedPath(MediaStore.Files.getContentUri("external"), "" + image.id)
                videoDurationHolder[image.id] = ImagePickerUtils.getVideoDurationLabel(
                    context,  uri
                )
            }

            fileTypeLabel = videoDurationHolder[image.id]
            showFileTypeIndicator = true
        }

        viewHolder.apply {
            fileTypeIndicator.text = fileTypeLabel
            fileTypeIndicator.visibility = if (showFileTypeIndicator) View.VISIBLE else View.GONE
            alphaView.alpha = if (isSelected) 0.5f else 0f
            itemView.setOnClickListener {
                val shouldSelect = itemClickListener.onImageClick(isSelected)

                if (isSelected) {
                    removeSelectedImage(image, position)
                } else if (shouldSelect) {
                    addSelected(image, position)
                }
            }
            container?.foreground = if (isSelected) ContextCompat.getDrawable(
                context,
                R.drawable.ef_ic_done_white
            ) else null
        }
    }

    private fun isSelected(image: Image): Boolean {
        return selectedImages.any { it.path == image.path }
    }

    override fun getItemCount() = images.size

    fun setData(images: List<Image>) {
        this.images.clear()
        this.images.addAll(images)
    }

    private fun addSelected(image: Image, position: Int) {
        mutateSelection {
            selectedImages.add(image)
            notifyItemChanged(position)
        }
    }

    private fun removeSelectedImage(image: Image, position: Int) {
        mutateSelection {
            selectedImages.add(image)
            notifyItemChanged(position)
        }
    }

    fun removeAllSelectedSingleClick() {
        mutateSelection {
            selectedImages.clear()
            notifyDataSetChanged()
        }
    }

    private fun mutateSelection(runnable: () -> Unit) {
        runnable()
        imageSelectedListener?.onSelectionUpdate(selectedImages)
    }

    fun setImageSelectedListener(imageSelectedListener: OnImageSelectedListener?) {
        this.imageSelectedListener = imageSelectedListener
    }

    fun getItem(position: Int) = images.getOrNull(position)

    class ImageViewHolder(itemView: View) : ViewHolder(itemView) {
        val imageView: ImageView = itemView.image_view
        val alphaView: View = itemView.view_alpha
        val fileTypeIndicator: TextView = itemView.ef_item_file_type_indicator
        val container: FrameLayout? = itemView as? FrameLayout
    }
}