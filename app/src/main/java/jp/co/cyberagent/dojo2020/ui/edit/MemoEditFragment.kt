package jp.co.cyberagent.dojo2020.ui.edit

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import jp.co.cyberagent.dojo2020.R
import jp.co.cyberagent.dojo2020.databinding.FragmentMemoEditBinding
import jp.co.cyberagent.dojo2020.ui.create.MemoEditViewModelFactory
import jp.co.cyberagent.dojo2020.ui.create.spinner.CustomOnItemSelectedListener
import jp.co.cyberagent.dojo2020.ui.create.spinner.SpinnerAdapter
import jp.co.cyberagent.dojo2020.ui.widget.CustomBottomSheetDialog

class MemoEditFragment : Fragment() {

    val args: MemoEditFragmentArgs by navArgs()

    private lateinit var activityInFragment: AppCompatActivity
    private lateinit var binding: FragmentMemoEditBinding

    private val memoEditViewModel by activityViewModels<MemoEditViewModel> {
        MemoEditViewModelFactory(this, Bundle(), requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMemoEditBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            val spinnerAdapter = SpinnerAdapter.getInstance(requireContext()).apply {
                setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            }

            profileIconImageButton.setOnClickListener {
                showProfileScreen()
            }

            val memoId = args.memoId

            memoEditViewModel.memoInfoLiveData.observe(viewLifecycleOwner) {
                titleTextEdit.setText(it.title)
                contentTextEdit.setText(it.content)
                //
            }

            memoEditViewModel.fetchMemo(memoId)

            memoEditViewModel.userLiveData.observe(viewLifecycleOwner) { firebaseUserInfo ->
                val uri = firebaseUserInfo?.imageUri ?: return@observe

                profileIconImageButton.showImage(uri)
            }

            categorySpinner.apply {
                adapter = spinnerAdapter
                onItemSelectedListener = CustomOnItemSelectedListener(
                    this@MemoEditFragment::showDialog
                )

                setSelection(1)
            }

            memoEditViewModel.categoryListLiveData.observe(viewLifecycleOwner) { categoryList ->
                spinnerAdapter.apply {
                    clear()
                    addAll(SpinnerAdapter.defaultItemList(context))
                    addAll(categoryList)
                    notifyDataSetChanged()
                }
            }

            editButton.setOnClickListener {
                val title = titleTextEdit.text.toString()
                val content = contentTextEdit.text.toString()
                val category = categorySpinner.selectedItem.toString()

                memoEditViewModel.addDraft(title, content, category)

                findNavController().navigate(R.id.action_createMemoFragment_to_homeFragment)
            }
        }
    }

    private fun showDialog() {
        CustomBottomSheetDialog().apply {
            show(activityInFragment.supportFragmentManager, CustomBottomSheetDialog.TAG)
        }
    }

    private fun ImageButton.showImage(uri: Uri) {
        Glide.with(this).load(uri).circleCrop().into(this)
    }

    private fun showProfileScreen() {
        findNavController().navigate(R.id.action_memoEditFragment_to_profileFragment)
    }

}
