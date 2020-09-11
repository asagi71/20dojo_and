package jp.co.cyberagent.dojo2020.ui.profile

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate
import jp.co.cyberagent.dojo2020.R
import jp.co.cyberagent.dojo2020.data.model.Account
import jp.co.cyberagent.dojo2020.data.model.Profile
import jp.co.cyberagent.dojo2020.databinding.FragmentProfileBinding
import jp.co.cyberagent.dojo2020.databinding.LayoutAccountItemBinding
import jp.co.cyberagent.dojo2020.ui.widget.CustomBottomSheetDialog.Companion.TAG

class ProfileFragment : Fragment() {
    private lateinit var binding: FragmentProfileBinding

    private val viewModel: ProfileViewModel by viewModels {
        ProfileViewModelFactory(this, Bundle(), requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(inflater)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            viewModel.firebaseUserInfo.observe(viewLifecycleOwner) { firebaseUser ->
                firebaseUser ?: return@observe

                userNameTextView.text = firebaseUser.name

                Glide.with(view).load(firebaseUser.imageUri).circleCrop().into(iconImageView)
            }

            viewModel.profileLiveData.observe(viewLifecycleOwner) {
                val itemViewList = listOf(firstAccountItemView, secondAccountItemView)

                // require feature editing
                val profile = Profile(
                    "", "", listOf(
                        Account("GitHub", "ShebangDog", "https://github.com/ShebangDog"),
                        Account("Twitter", "ShebangDog", "https://twitter.com/ShebangDog")
                    )
                )

                val imageList = listOf(R.mipmap.github_logo, R.mipmap.twitter_logo)

                data class Group(
                    val itemView: LayoutAccountItemBinding,
                    val account: Account,
                    val image: Int
                )

                val list = itemViewList zip profile.accountList.orEmpty() zip imageList
                val groupList = list.map { (tuple, profile) ->
                    val (itemView, account) = tuple

                    Group(itemView, account, profile)
                }

                groupList.forEach { (itemView, account, image) ->
                    itemView.apply {
                        idTextView.text = "@${account.id}"
                        urlTextView.text = account.url
                        iconImageView.setImageResource(image)
                    }
                }
            }

            viewModel.totalTimeLiveData.observe(viewLifecycleOwner) {
                profileTotalTime.text = it.toString()
            }

            viewModel.pieEntryListLiveData.observe(viewLifecycleOwner) {
                Log.d(TAG, it.joinToString())
                showPieChart(it)
            }

        }
    }

    private fun showPieChart(entryList: List<PieEntry>) {
        val dataSet = PieDataSet(entryList, "category")
        dataSet.apply {
            valueTextSize = 12f
            valueTextColor = Color.WHITE

            setColors(*ColorTemplate.JOYFUL_COLORS)
        }

        binding.pieChart.apply {
            data = PieData(dataSet)
            centerText = "Statics"

            setEntryLabelTextSize(13f)
            setEntryLabelColor(Color.BLACK)
            setCenterTextSize(15f)
            animateY(750)
            invalidate()
        }
    }

}