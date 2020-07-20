/*
 * (c) VAP Communications Group, 2020
 */

package online.vapcom.skyword.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import kotlinx.android.synthetic.main.fatal_error.view.*
import kotlinx.android.synthetic.main.fragment_word_details.view.*
import online.vapcom.skyword.R
import online.vapcom.skyword.SkywordApplication
import online.vapcom.skyword.data.ErrorDescription
import online.vapcom.skyword.databinding.FragmentWordDetailsBinding
import online.vapcom.skyword.ui.states.DetailsState
import online.vapcom.skyword.ui.states.ScreenState
import online.vapcom.skyword.viewmodels.DetailsViewModel

private const val TAG = "WordDetsFr"

/**
 * Фрагмент подробного описания найденного слова
 */
class WordDetailsFragment: BaseFragment() {
    private lateinit var viewModel: DetailsViewModel
    private lateinit var binding: FragmentWordDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.i(TAG, "-- onCreate")

        val repo = (requireContext().applicationContext as SkywordApplication).dictRepository
        viewModel = ViewModelProvider(this, DetailsViewModel.FACTORY(repo)).get(DetailsViewModel::class.java)
        subscribeUI()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_word_details, container, false)
        root = binding.root

        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner


        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupUI()
        viewModel.setup(arguments?.getString(ArgsTags.ARG_MEANING_ID) ?: "")
    }

    private fun subscribeUI() {
        viewModel.state.observe(::getLifecycle, ::updateUI)
    }

    private fun setupUI() {

        root.toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }

    }

    /**
     * Обработчик индикаций и ответов от viewModel
     */
    private fun updateUI(screenState: ScreenState<DetailsState>?) {
        Log.i(TAG, "-- updateUI: $screenState")

        when (screenState) {
            ScreenState.Loading -> showProgress()
            is ScreenState.Render -> processRenderState(screenState.renderState)
        }
    }


    private fun showProgress() {
        Log.i(TAG, "-- SHOW Progress")
        root.progress.visibility = View.VISIBLE
        root.fatal_error.visibility = View.GONE
    }

    private fun hideProgress() {
        Log.i(TAG, "-- HIDE Progress")
        root.progress.visibility = View.GONE
        root.fatal_error.visibility = View.GONE
    }

    /**
     * Обработка индикаций от viewModel.
     *
     */
    private fun processRenderState(renderState: DetailsState) {
        Log.i(TAG, "-- processRenderState: $renderState")

        hideProgress()
        when (renderState) {
            is DetailsState.Ready -> {} // здесь пока ничего не делаем, все данные отображаются через байдинг
            is DetailsState.ShowError -> showError(renderState.error)
        }
    }


    override fun onShowFatalError() {
        root.progress.visibility = View.GONE
        root.content.visibility = View.GONE
        root.fatal_error.visibility = View.VISIBLE
    }

    override fun showCommonError(error: ErrorDescription): Boolean = false
}