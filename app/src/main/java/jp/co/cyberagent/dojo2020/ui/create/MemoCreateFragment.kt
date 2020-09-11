package jp.co.cyberagent.dojo2020.ui.create

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import jp.co.cyberagent.dojo2020.R
import jp.co.cyberagent.dojo2020.databinding.FragmentMemoCreateBinding
import jp.co.cyberagent.dojo2020.ui.create.spinner.CustomOnItemSelectedListener
import jp.co.cyberagent.dojo2020.ui.create.spinner.SpinnerAdapter
import jp.co.cyberagent.dojo2020.ui.widget.CustomBottomSheetDialog
import jp.co.cyberagent.dojo2020.ui.widget.CustomBottomSheetDialog.Companion.TAG

class MemoCreateFragment : Fragment() {
    private lateinit var activityInFragment: AppCompatActivity
    private lateinit var binding: FragmentMemoCreateBinding

    private val memoCreateViewModel by activityViewModels<MemoCreateViewModel> {
        MemoCreateViewModelFactory(this, Bundle(), requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMemoCreateBinding.inflate(inflater)

        return binding.root
    }

    override fun onResume() {
        super.onResume()

        showKeyboard()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        if (context is AppCompatActivity) {
            activityInFragment = context
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            val spinnerAdapter = SpinnerAdapter.getInstance(requireContext()).apply {
                setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            }

            profileIconImageButton.setOnClickListener { showProfileScreen() }

            memoCreateViewModel.userLiveData.observe(viewLifecycleOwner) { firebaseUserInfo ->
                val uri = firebaseUserInfo?.imageUri ?: return@observe

                profileIconImageButton.showImage(uri)
            }

            categorySpinner.apply {
                adapter = spinnerAdapter
                onItemSelectedListener = CustomOnItemSelectedListener(
                    this@MemoCreateFragment::showDialog
                )

                setSelection(1)
            }

            memoCreateViewModel.categoryListLiveData.observe(viewLifecycleOwner) { categoryList ->
                spinnerAdapter.apply {
                    clear()
                    addAll(SpinnerAdapter.defaultItemList(context))
                    addAll(categoryList)
                    notifyDataSetChanged()
                }
            }

            addButton.setOnClickListener {
                val title = titleTextEdit.text.toString()
                val content = contentTextEdit.text.toString()
                val category = categorySpinner.selectedItem.toString()

                Log.d(TAG, "onClick in MemoCreateFragment")
                memoCreateViewModel.addDraft(title, content, category)

                findNavController().navigate(R.id.action_createMemoFragment_to_homeFragment)
            }
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setHasOptionsMenu(true)
    }

    private fun showKeyboard() {
        val manager =
            activityInFragment.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        manager.showSoftInput(binding.titleTextEdit, InputMethodManager.SHOW_IMPLICIT)
    }

    private fun showDialog() {
        CustomBottomSheetDialog().apply {
            show(activityInFragment.supportFragmentManager, TAG)
        }
    }

    private fun ImageButton.showImage(uri: Uri) {
        Glide.with(this).load(uri).circleCrop().into(this)
    }

    private fun showProfileScreen() {
        findNavController().navigate(R.id.action_memoCreateFragment_to_profileFragment)
    }
}
