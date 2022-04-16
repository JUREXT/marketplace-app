package com.narcissus.marketplace.ui.checkout

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.airbnb.lottie.LottieDrawable
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_EXPANDED
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.hannesdorfmann.adapterdelegates4.AsyncListDifferDelegationAdapter
import com.narcissus.marketplace.R
import com.narcissus.marketplace.core.util.launchWhenStarted
import com.narcissus.marketplace.databinding.FragmentCheckoutBinding
import com.narcissus.marketplace.domain.card.CardValidationResult
import com.narcissus.marketplace.domain.model.CheckoutItem
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.component.KoinComponent
import ru.tinkoff.decoro.MaskImpl
import ru.tinkoff.decoro.parser.UnderscoreDigitSlotsParser
import ru.tinkoff.decoro.slots.PredefinedSlots
import ru.tinkoff.decoro.watchers.MaskFormatWatcher

class CheckoutFragment : BottomSheetDialogFragment(), KoinComponent {
    private var _binding: FragmentCheckoutBinding? = null
    private val binding get() = _binding!!

    private val viewModel: CheckoutViewModel by viewModel()

    private val adapter by lazy {
        AsyncListDifferDelegationAdapter(
            CheckoutListItem.DIFF_CALLBACK,
            CheckoutListItem.Detail.delegate(),
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentCheckoutBinding.bind(
            inflater.inflate(R.layout.fragment_checkout, container),
        )

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.rvCheckoutDetails.adapter = adapter
        configureBottomSheetBehaviour()
        initPlaceOrderButton()
        initDummyView()
        setMaskOnCard()
        subscribeToViewModel()
    }

    private fun configureBottomSheetBehaviour() {
        with(dialog as BottomSheetDialog) {
            behavior.state = STATE_EXPANDED
            behavior.skipCollapsed = true
        }
    }

    private fun initDummyView() {
        binding.root.viewTreeObserver.addOnGlobalLayoutListener {
            val sheetHeight = binding.clContent.height
            val windowHeight = requireActivity().resources.displayMetrics.heightPixels
            val placeholderHeight = windowHeight - sheetHeight

            if (placeholderHeight > 0) {
                binding.dummy.layoutParams.height = placeholderHeight
                binding.dummy.requestLayout()
            }
        }
    }

    private fun setMaskOnCard() {
        setMaskOnCardNumber()
        setMaskOnExpireDate()
    }

    private fun subscribeToViewModel() {
        observeCardValidationResult()
        observeScreenState()
    }

    private fun observeScreenState() {
        viewModel.screenStateFlow
            .onEach(::displayState)
            .launchWhenStarted(viewLifecycleOwner.lifecycleScope)
    }

    private fun displayState(state: CheckoutScreenState) {
        when (state) {
            is CheckoutScreenState.Loading -> {
                binding.groupCheckout.isVisible = false
                binding.groupStatus.isVisible = true
                showLoadingAnim()
            }
            is CheckoutScreenState.Idle -> {
                binding.groupCheckout.isVisible = true
                binding.groupStatus.isVisible = false
                showCheckoutContent(state.items, state.totalCost)
            }
            is CheckoutScreenState.PaymentFailed -> {
                binding.groupCheckout.isVisible = false
                binding.groupStatus.isVisible = true
                showFailure(state.message)
            }
            is CheckoutScreenState.PaymentSuccessful -> {
                binding.groupCheckout.isVisible = false
                binding.groupStatus.isVisible = true
                showSuccess(state.message)
            }
        }
    }

    private fun showLoadingAnim() {
        binding.lottieAnim.repeatCount = LottieDrawable.INFINITE
        binding.lottieAnim.setAnimation("circular_progress.json")
    }

    private fun showFailure(message: String) {
        binding.lottieAnim.repeatCount = LottieDrawable.INFINITE
        binding.lottieAnim.setAnimation("failure.json")
        binding.tvMessage.text = message
    }

    private fun showSuccess(message: String) {
        binding.lottieAnim.repeatCount = 1
        binding.lottieAnim.setAnimation("success.json")
        binding.tvMessage.text = message
    }

    private fun showCheckoutContent(items: List<CheckoutItem>, totalCost: Int) {
        adapter.items = items.map { checkoutItem ->
            checkoutItem.toCheckoutListItem()
        }

        binding.tvOrderTotalPrice.text = requireContext()
            .getString(R.string.price_placeholder, totalCost)
    }

    private fun observeCardValidationResult() {
        viewModel.cardValidationResultFlow.onEach { result ->
            when (result) {
                is CardValidationResult.InvalidCardHolderName -> showCardHolderNameError()
                is CardValidationResult.InvalidCardNumber -> showCardNumberError()
                is CardValidationResult.InvalidExpirationDate -> showCardExpireDateError()
                is CardValidationResult.InvalidCvv -> showCardCvvError()
                is CardValidationResult.Success -> viewModel.proceedWithOrderPlacement()
            }
        }.launchIn(viewLifecycleOwner.lifecycleScope)
    }

    private fun showCardHolderNameError() {
        binding.etCardHolder.error = getString(R.string.name_empty)
    }

    private fun showCardNumberError() {
        binding.etCardNumber.error = getString(R.string.invalid_card_number)
    }

    private fun showCardExpireDateError() {
        binding.etMonthYear.error = getString(R.string.invalid_expire_date)
    }

    private fun showCardCvvError() {
        binding.etCvv.error = getString(R.string.invalid_cvv)
    }

    private fun setMaskOnCardNumber() {
        val maskCardNumber = MaskImpl.createTerminated(PredefinedSlots.CARD_NUMBER_STANDARD)
        MaskFormatWatcher(maskCardNumber).installOn(binding.etCardNumber)
    }

    private fun setMaskOnExpireDate() {
        val slots = UnderscoreDigitSlotsParser().parseSlots(MASK_MONTH_YEAR)
        MaskFormatWatcher(MaskImpl.createTerminated(slots)).installOn(binding.etMonthYear)
    }

    private fun validateCard() {
        viewModel.validateCard(
            binding.etCardHolder.text.toString(),
            binding.etCardNumber.text.toString(),
            binding.etMonthYear.text.toString(),
            binding.etCvv.text.toString(),
        )
    }

    private fun initPlaceOrderButton() {
        binding.btnPlaceOrder.setOnClickListener {
            validateCard()
        }
    }

    private fun CheckoutItem.toCheckoutListItem() = CheckoutListItem.Detail(this)

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        requireContext().sendBroadcast(
            Intent(OrderConstants.PAY_INTENT_FILTER).putExtra(
                viewModel.orderId,
                true,
            ),
        )
    }

    private companion object {
        const val MASK_MONTH_YEAR = "__/__"
    }
}
