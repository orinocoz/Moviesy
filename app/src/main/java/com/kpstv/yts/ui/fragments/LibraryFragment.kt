package com.kpstv.yts.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.kpstv.yts.R
import com.kpstv.yts.adapters.LibraryDownloadAdapter
import com.kpstv.yts.cast.CastHelper
import com.kpstv.yts.extensions.Coroutines
import com.kpstv.yts.extensions.deleteRecursive
import com.kpstv.yts.extensions.hide
import com.kpstv.yts.extensions.show
import com.kpstv.yts.ui.activities.MainActivity
import com.kpstv.yts.ui.activities.SearchActivity
import com.kpstv.yts.ui.dialogs.AlertNoIconDialog
import com.kpstv.yts.ui.fragments.sheets.BottomSheetLibraryDownload
import com.kpstv.yts.ui.fragments.sheets.PlaybackType
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.fragment_library.view.*
import kotlinx.android.synthetic.main.fragment_library_no_download.view.*
import kotlinx.android.synthetic.main.fragment_library.view.toolbar
import java.io.File

class LibraryFragment : Fragment() {

    private lateinit var mainActivity: MainActivity
    private val TAG = "LibraryFragment"
    private lateinit var downloadAdapter: LibraryDownloadAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mainActivity = activity as MainActivity

        mainActivity.viewModel.librayView?.let {
            return it
        } ?: return inflater.inflate(R.layout.fragment_library, container, false).also { view ->

            view.layout_noDownload.hide()

            setToolBar(view)

            setRecyclerView(view)

            bindUI(view)

            mainActivity.viewModel.librayView = view
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainActivity.castHelper.init(mainActivity, view.toolbar)
    }

    private fun setRecyclerView(view: View) {
        view.recyclerView_download.layoutManager = LinearLayoutManager(context)
        downloadAdapter = LibraryDownloadAdapter(requireContext(), ArrayList())
        downloadAdapter.OnClickListener = { model, _ ->
            /** OnClick for download item */
            val sheet = if (mainActivity.castHelper.isCastActive()) {
                /** Show cast to device button */
                BottomSheetLibraryDownload(mainActivity.viewModel, PlaybackType.REMOTE)
            }else {
                /** Show local play button */
                BottomSheetLibraryDownload(mainActivity.viewModel, PlaybackType.LOCAL)
            }
            val bundle = Bundle()
            bundle.putSerializable("model", model)
            sheet.arguments = bundle
            sheet.show(mainActivity.supportFragmentManager, "")
        }
        downloadAdapter.OnMoreClickListener = { innerView, model, _ ->
            val popupMenu = PopupMenu(requireContext(), innerView)
            popupMenu.inflate(R.menu.library_menu)
            popupMenu.setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.action_show_location -> {
                        AlertNoIconDialog.Companion.Builder(context).apply {
                            setTitle("Location")
                            setMessage("${model.downloadPath}")
                            setPositiveButton("OK", null)
                        }.show()
                    }
                    R.id.action_delete -> {
                        AlertNoIconDialog.Companion.Builder(context).apply {
                            setTitle("Delete?")
                            setMessage("This can't be undone?")
                            setNegativeButton("Nope", object : AlertNoIconDialog.DialogListener {
                                override fun onClick() {}
                            })
                            setPositiveButton("Do It", object : AlertNoIconDialog.DialogListener {
                                override fun onClick() {
                                    val f = File(model.downloadPath!!)
                                    if (f.exists()) {
                                        f.deleteRecursive()
                                    } else {
                                        Toasty.error(
                                            requireContext(),
                                            "Path does not exist",
                                            Toasty.LENGTH_SHORT
                                        ).show()
                                        mainActivity.viewModel.removeDownload(model.hash)
                                    }
                                }
                            })
                        }.show()
                    }
                }
                return@setOnMenuItemClickListener true
            }
            popupMenu.show()
        }
        view.recyclerView_download.adapter = downloadAdapter
    }

    private fun bindUI(view: View) = Coroutines.main {
        mainActivity.viewModel.downloadMovieIds.await().observe(mainActivity, Observer {
            downloadAdapter.updateModels(it)
            if (it.isNotEmpty()) {
                view.layout_noDownload.hide()
                view.fl_downloadLayout.show()
            } else {
                view.layout_noDownload.show()
                view.fl_downloadLayout.hide()
            }
        })
    }

    private fun setToolBar(view: View) {
        view.toolbar.title = getString(R.string.library)
        view.toolbar.setNavigationIcon(R.drawable.ic_menu)
        view.toolbar.setNavigationOnClickListener {
            mainActivity.drawerLayout.openDrawer(GravityCompat.START)
        }
        view.toolbar.inflateMenu(R.menu.fragment_library_menu)

        view.toolbar.setOnMenuItemClickListener {
            if (it.itemId == R.id.action_search) {
                val intent = Intent(mainActivity, SearchActivity::class.java)
                startActivity(intent)
            }
            return@setOnMenuItemClickListener true
        }
    }
}
