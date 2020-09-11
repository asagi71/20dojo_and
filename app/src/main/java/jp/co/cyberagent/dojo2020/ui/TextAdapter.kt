package jp.co.cyberagent.dojo2020.ui

import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import jp.co.cyberagent.dojo2020.R
import jp.co.cyberagent.dojo2020.data.model.Draft
import jp.co.cyberagent.dojo2020.data.model.Memo
import jp.co.cyberagent.dojo2020.databinding.LayoutMemoItemBinding
import jp.co.cyberagent.dojo2020.util.Left
import jp.co.cyberagent.dojo2020.util.Right
import jp.co.cyberagent.dojo2020.util.Text
import java.util.Collections.emptyList

class TextAdapter(
    private val onItemClickListener: (id: String) -> Unit,
    private val saveDraftAsMemo: (Draft) -> Unit
) : RecyclerView.Adapter<TextAdapter.RecyclerViewHolder>() {

    var textList: List<Text> = emptyList()
        set(value) {
            field = value
            Log.d("Content", "onChange textList in TextAdapter")
            notifyDataSetChanged()
        }

    class RecyclerViewHolder(
        private val binding: LayoutMemoItemBinding,
        private val saveDraftAsMemo: (Draft) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        private val context = binding.root.context
        private val startingImage = context.getDrawable(R.drawable.starting_timer)
        private val stoppingImage = context.getDrawable(R.drawable.stopping_timer)

        fun setOnItemClickListener(onItemClickListener: View.OnClickListener) {
            itemView.setOnClickListener(onItemClickListener)
        }

        fun setText(text: Text) {
            fun ImageButton.showImage(drawable: Drawable?) {
                Glide.with(this).load(drawable).into(this)
            }

            var (mutableImage, isStarting) = when (text) {
                is Left -> stoppingImage to false
                is Right -> startingImage to true
            }
            val image = mutableImage

            binding.timerImageButton.apply {
                showImage(image)

                setOnClickListener {
                    isStarting = !isStarting

                    showImage(if (isStarting) startingImage else stoppingImage)
                }
            }

            when (text) {
                is Left -> setDraft(text.value)
                is Right -> setMemo(text.value)
            }
        }

        private fun setMemo(memo: Memo) {
            binding.apply {
                titleTextView.text = memo.title
                contentsTextView.text = "Memo" + memo.contents
                categoryTextView.text = memo.category

                chronometer.apply {
                    stop()

                    text = memo.time.toString()
                }
            }
        }

        private fun setDraft(draft: Draft) {
            binding.apply {
                titleTextView.text = draft.title
                contentsTextView.text = "Draft" + draft.content
                categoryTextView.text = draft.category

                chronometer.apply {
                    start()
                }

                timerImageButton.setOnClickListener {
                    chronometer.stop()
                    saveDraftAsMemo(draft)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = LayoutMemoItemBinding.inflate(inflater, parent, false)

        return RecyclerViewHolder(binding, saveDraftAsMemo)
    }

    override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) {
        val text = textList[position]

        holder.setText(text)
        holder.setOnItemClickListener {
            onItemClickListener(
                when (text) {
                    is Left -> text.value.id
                    is Right -> text.value.id
                }
            )
        }
    }

    override fun getItemCount() = textList.size

}