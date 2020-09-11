package jp.co.cyberagent.dojo2020.ui.home

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.asLiveData
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import jp.co.cyberagent.dojo2020.R
import jp.co.cyberagent.dojo2020.data.model.Draft
import jp.co.cyberagent.dojo2020.data.model.Memo
import jp.co.cyberagent.dojo2020.databinding.FragmentHomeBinding
import jp.co.cyberagent.dojo2020.ui.TextAdapter
import jp.co.cyberagent.dojo2020.ui.widget.CustomBottomSheetDialog.Companion.TAG
import jp.co.cyberagent.dojo2020.util.Left
import jp.co.cyberagent.dojo2020.util.Text
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.observeOn

class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private val homeViewModel by activityViewModels<HomeViewModel> {
        HomeViewModelFactory(this, Bundle(), requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater)

        return binding.root
    }

    @FlowPreview
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            createMemoFloatingActionButton.setOnClickListener { showMemoCreateScreen() }
            profileIconImageButton.setOnClickListener { showProfileScreen() }

            val textAdapter = TextAdapter(
                { showMemoEditScreen(it) },
                {
                    val memoForSave = it.toMemo(System.currentTimeMillis())
                    homeViewModel.deleteDraft(it)
                    homeViewModel.saveMemo(memoForSave)
                }
            )
          
            val linearLayoutManager = LinearLayoutManager(
                context,
                LinearLayoutManager.VERTICAL,
                false
            )

            recyclerView.apply {
                setHasFixedSize(true)
                layoutManager = linearLayoutManager
                adapter = textAdapter
            }

            homeViewModel.userLiveData.observe(viewLifecycleOwner) { firebaseUserInfo ->
                val uri = firebaseUserInfo?.imageUri ?: return@observe

                profileIconImageButton.showImage(uri)
            }

            homeViewModel.textListLiveData.observe(viewLifecycleOwner) { textList ->
                Log.d(TAG, "onChange textListLiveData")
                textAdapter.textList = textList
            }

        }
    }

    private fun showMemoCreateScreen() {
        findNavController().navigate(R.id.action_homeFragment_to_memoCreateFragment)
    }

    private fun showProfileScreen() {
        findNavController().navigate(R.id.action_homeFragment_to_profileFragment)
    }

    private fun showMemoEditScreen(id: String) {
        val action = HomeFragmentDirections.actionHomeFragmentToMemoEditFragment(id)
        findNavController().navigate(action)
    }

    private fun ImageButton.showImage(uri: Uri) {
        Glide.with(this).load(uri).circleCrop().into(this)
    }

    private fun List<Draft>.toText(): List<Text> = map {
        Left(it)
    }

    private fun Draft.toMemo(endTime: Long): Memo {
        return Memo(id, title, content, (endTime - startTime) / 1000, category)
    }
}