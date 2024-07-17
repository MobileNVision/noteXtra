package com.mobilenvision.notextra.ui.daily

import android.app.DatePickerDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.activity.addCallback
import androidx.lifecycle.ViewModelProvider
import com.mobilenvision.notextra.BR
import com.mobilenvision.notextra.R
import com.mobilenvision.notextra.data.model.db.Daily
import com.mobilenvision.notextra.databinding.FragmentDailyBinding
import com.mobilenvision.notextra.di.component.FragmentComponent
import com.mobilenvision.notextra.ui.addDaily.AddDailyFragment
import com.mobilenvision.notextra.ui.addDaily.FullScreenImageDialogFragment
import com.mobilenvision.notextra.ui.base.BaseFragment
import com.mobilenvision.notextra.utils.CommonUtils
import com.wajahatkarim3.easyflipviewpager.BookFlipPageTransformer2
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject


class DailyFragment : BaseFragment<FragmentDailyBinding, DailyViewModel>(), DailyNavigator, DailyAdapter.DailyAdapterListener {
    private var font: String = ""
    private lateinit var userId: String
    private lateinit var noteList: java.util.ArrayList<Daily>
    private lateinit var dailyAdapter: DailyAdapter

    @Inject
    lateinit var viewModel: DailyViewModel

    private lateinit var binding: FragmentDailyBinding
    override val bindingVariable: Int
        get() = BR.viewModel
    override val layoutId: Int
        get() = R.layout.fragment_daily


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentDailyBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(this)[DailyViewModel::class.java]
        viewModel.setNavigator(this)
        binding.viewModel = viewModel
        userId= mViewModel.getUserData().third.toString()
        font = mViewModel.getFontFamily()

        if(CommonUtils.isInternetAvailable(baseActivity!!)){
            mViewModel.getUnSynchronizedDailyNotes()
            mViewModel.syncDeletedDailyNotes()
        }
        val internetStatus = CommonUtils.isInternetAvailable(baseActivity!!)
        mViewModel.setInternetStatus(internetStatus)
        if(internetStatus){
            mViewModel.getDailyNotesFromFirebase(userId)
        }
        else {
            mViewModel.getListFromDatabase()
        }

    }

    companion object {
        const val TAG = "DailyFragment"
    }
    
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = fetchViewDataBinding()
        setupRecyclerView()
        showBottomNavigation()
        showToolbar()

        binding.smartSearchEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterDailyNotes(s.toString())
            }
        })
        baseActivity!!.onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            handleOnBackPress()
        }
    }

    private fun handleOnBackPress() {
        activity?.finish()
    }

    private fun filterDailyNotes(query: String) {
        val filteredList = noteList.filter {
                    (it.text?.contains(query, ignoreCase = true) ?: false)
        }
        updateAdapter(filteredList)
    }

    private fun updateAdapter(filteredList: List<Daily>) {
        dailyAdapter = DailyAdapter(emptyList(),font)
        binding.viewPager.adapter = dailyAdapter

        dailyAdapter.addItems(filteredList)
        dailyAdapter.setListener(this)
        dailyAdapter.setContext(requireContext())
        if (filteredList.isEmpty()) {
            binding.textEmptyState.visibility = View.VISIBLE
        } else {
            binding.textEmptyState.visibility = View.GONE
        }
    }

    private fun setupRecyclerView() {
        dailyAdapter = DailyAdapter(emptyList(), font)
        binding.viewPager.adapter = dailyAdapter
        dailyAdapter.setListener(this)
        dailyAdapter.setContext(requireContext())

    }
    override fun performDependencyInjection(buildComponent: FragmentComponent) {
        buildComponent.inject(this)
    }

    override fun addDailyButtonClick() {
        baseActivity?.loadFragment(AddDailyFragment.newInstance("",""), AddDailyFragment.TAG)

    }

    override fun emptyDailyList() {
        binding.textEmptyState.visibility = View.VISIBLE
        dailyAdapter = DailyAdapter(emptyList(), font)
        noteList = ArrayList()
    }

    override fun setDailyList(mDailyList: ArrayList<Daily>) {
        if (mDailyList.isEmpty()) {
            emptyDailyList()
            return
        }

        val today = getCurrentDateString()

        val todayList = mutableListOf<Daily>()

        val pastDaysList = mutableListOf<Daily>()

        val futureDaysList = mutableListOf<Daily>()

        mDailyList.forEach { daily ->
            val noteDate = daily.day

            if (noteDate == today) {
                todayList.add(daily)
            } else if (isPastDate(noteDate!!)) {
                pastDaysList.add(daily)
            } else {
                futureDaysList.add(daily)
            }
        }

        val updatedList = mutableListOf<Daily>().apply {
            addAll(pastDaysList.reversed())
            addAll(todayList)
            addAll(futureDaysList)
        }

        dailyAdapter = DailyAdapter(updatedList,font)
        dailyAdapter.addItems(updatedList)
        noteList = ArrayList(updatedList)
        binding.viewPager.adapter = dailyAdapter
        val bookFlipPageTransformer = BookFlipPageTransformer2()
        bookFlipPageTransformer.isEnableScale = true
        bookFlipPageTransformer.scaleAmountPercent = 10f
        binding.viewPager.setPageTransformer(bookFlipPageTransformer)
        val todayIndex = updatedList.indexOfFirst { it.day == today }
        if (todayIndex != -1) {
            binding.viewPager.setCurrentItem(todayIndex, true)
        }
        if (updatedList.isEmpty()) {
            binding.textEmptyState.visibility = View.VISIBLE
        } else {
            binding.textEmptyState.visibility = View.GONE
        }
    }

    private fun getCurrentDateString(): String {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return dateFormat.format(Calendar.getInstance().time)
    }

    private fun isPastDate(dateString: String): Boolean {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val currentDate = Calendar.getInstance().time
        val noteDate = dateFormat.parse(dateString)

        return noteDate != null && noteDate.before(currentDate)
    }



    override fun onFailure(message: String?) {
        showToast(message!!)
    }

    override fun deleteDailySuccess() {
        showToast(baseActivity!!.getString(R.string.delete_success))
        mViewModel.getListFromDatabase()
    }


    override fun onSuccessAddDailyNotes() {
        showToast(getString(R.string.synchronized_success))
    }

    override fun deleteDailySuccessToDatabase() {
        showToast(getString(R.string.delete_to_database_success))
    }

    override fun getDayClick() {
        val datePickerDialog = DatePickerDialog(requireContext(), { _, year, month, dayOfMonth ->
            val calendar = Calendar.getInstance()
            calendar.set(year, month, dayOfMonth)
            val selectedDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(calendar.time)

            val index = noteList.indexOfFirst { it.day == selectedDate }
            if (index != -1) {
                binding.viewPager.currentItem = index
            }
        }, Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH), Calendar.getInstance().get(Calendar.DAY_OF_MONTH))
        datePickerDialog.show()
    }

    override fun onDailyItemClick(daily: Daily) {
    }

    override fun onImagesItemClick(imageUrl: String) {
        FullScreenImageDialogFragment.newInstance(imageUrl)
            .show(baseActivity!!.supportFragmentManager, "FullScreenImageDialogFragment")
    }


}