package com.kpstv.yts.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.tabs.TabLayout
import com.kpstv.common_moviesy.extensions.viewBinding
import com.kpstv.yts.R
import com.kpstv.yts.databinding.FragmentHomeBinding
import com.kpstv.yts.extensions.YTSQuery
import com.kpstv.yts.extensions.common.CustomMovieLayout
import com.kpstv.yts.ui.activities.AbstractBottomNavActivity
import com.kpstv.yts.ui.activities.MainActivity
import com.kpstv.yts.ui.activities.SearchActivity
import com.kpstv.yts.ui.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.fragment_home), TabLayout.OnTabSelectedListener,
    AbstractBottomNavActivity.BottomNavFragmentSelection {

    interface HomeFragmentCallbacks {
        fun doOnReselection() { }
    }

    private val viewModel by viewModels<MainViewModel>(
        ownerProducer = { requireActivity() }
    )
    private val binding by viewBinding(FragmentHomeBinding::bind)

    private lateinit var mainActivity: MainActivity

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainActivity = activity as MainActivity

        binding.searchImage.setOnClickListener {
            mainActivity.drawerLayout.openDrawer(GravityCompat.START)
        }

        binding.searchCard.setOnClickListener {
            val intent = Intent(mainActivity, SearchActivity::class.java)
            startActivity(intent)
        }

        binding.tvFilter.setOnClickListener {
            val queryMap = YTSQuery.ListMoviesBuilder.getDefault().apply {
                setQuality(YTSQuery.Quality.q2160p)
            }.build()
            CustomMovieLayout.invokeMoreFunction(requireContext(), getString(R.string.search_filters), queryMap)
        }

        binding.tabLayout.addOnTabSelectedListener(this)

        /** Restore tab position */
        setCurrentTab(viewModel.homeFragmentState.tabPosition)

        /** Restore app bar state */
        if (viewModel.homeFragmentState.isAppBarExpanded == false)
            binding.appBarLayout.collapse()
    }

    override fun onTabReselected(tab: TabLayout.Tab?) {}

    override fun onTabUnselected(tab: TabLayout.Tab?) {}

    override fun onTabSelected(tab: TabLayout.Tab?) {
        setCurrentTab(tab?.position)
    }

    override fun onReselected() {
        binding.appBarLayout.setExpanded(true)
        val fragment = childFragmentManager.findFragmentByTag(CURRENT_FRAGMENT) ?: return
        if (fragment is HomeFragmentCallbacks) {
            fragment.doOnReselection()
        }
    }

    private fun setCurrentTab(position: Int?) {
        if (position == 0 || position == null) {
            binding.tabLayout.getTabAt(0)?.select()
            setFragment(ChartsFragment())
        } else if (position == 1) {
            binding.tabLayout.getTabAt(1)?.select()
            setFragment(GenreFragment())
        }
    }

    private fun setFragment(fragment: Fragment) {
        childFragmentManager
            .beginTransaction()
            .replace(R.id.container, fragment, CURRENT_FRAGMENT)
            .commit()
    }

    /**
     * Save your state here
     */
    override fun onStop() {
        super.onStop()
        viewModel.homeFragmentState.isAppBarExpanded =
            binding.appBarLayout.isAppBarExpanded
        viewModel.homeFragmentState.tabPosition =
            binding.tabLayout.selectedTabPosition
    }

    companion object {
        private const val CURRENT_FRAGMENT = "currentFragment"
    }
}
