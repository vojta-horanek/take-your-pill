package eu.vojtechh.takeyourpill.view

import android.content.Context
import android.content.res.ColorStateList
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import com.shawnlin.numberpicker.NumberPicker
import eu.vojtechh.takeyourpill.R
import eu.vojtechh.takeyourpill.klass.onClick
import eu.vojtechh.takeyourpill.model.ReminderOptions

class PillOptionsView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    init {
        init(attrs)
    }

    private var onChangeAction: () -> Unit = {}
    private lateinit var radioGroup: RadioGroup
    private lateinit var radioIndefinitely: RadioButton
    private lateinit var radioXDays: RadioButton
    private lateinit var radioCycle: RadioButton

    private lateinit var layoutXDays: LinearLayout
    private lateinit var layoutCycle: LinearLayout

    private lateinit var layoutXDaysDuration: LinearLayout

    private lateinit var layoutCycleActive: LinearLayout
    private lateinit var layoutCycleInactive: LinearLayout
    private lateinit var layoutCycleToday: LinearLayout

    private lateinit var xDaysCountDuration: TextView

    private lateinit var cycleCountActive: TextView
    private lateinit var cycleCountInactive: TextView
    private lateinit var cycleCountToday: TextView

    private lateinit var divider: View

    private var reminderOptions = ReminderOptions.empty()

    private fun init(attrs: AttributeSet?) {
        val view = View.inflate(context, R.layout.layout_pill_options_view, this)

        orientation = VERTICAL

        radioGroup = view.findViewById(R.id.pill_options_view_radio)
        radioIndefinitely = view.findViewById(R.id.radio_indefinitely)
        radioXDays = view.findViewById(R.id.radio_x_days)
        radioCycle = view.findViewById(R.id.radio_cycle)

        layoutXDays = view.findViewById(R.id.layout_x_days)
        layoutCycle = view.findViewById(R.id.layout_cycle)

        layoutXDaysDuration = view.findViewById(R.id.layout_x_days_duration)

        layoutCycleActive = view.findViewById(R.id.layout_cycle_active)
        layoutCycleInactive = view.findViewById(R.id.layout_cycle_inactive)
        layoutCycleToday = view.findViewById(R.id.layout_cycle_today)

        xDaysCountDuration = view.findViewById(R.id.x_days_count_duration)

        cycleCountActive = view.findViewById(R.id.cycle_count_active)
        cycleCountInactive = view.findViewById(R.id.cycle_count_inactive)
        cycleCountToday = view.findViewById(R.id.cycle_count_today)

        divider = view.findViewById(R.id.divider)

        val attributes = context.obtainStyledAttributes(attrs, R.styleable.PillOptionsView)
        try {
            val accentColor = attributes.getColor(
                R.styleable.PillOptionsView_accentColor,
                context.getColor(R.color.colorPrimary)
            )
            setAccentColor(accentColor)
        } finally {
            attributes.recycle()
        }

        radioGroup.setOnCheckedChangeListener { _, checkedId ->
            onCheckedChange(checkedId)
        }

        layoutXDaysDuration.onClick {
            showChangeItemDialog(
                context.getString(R.string.duration),
                1,
                999,
                reminderOptions.daysActive,
            ) {
                reminderOptions.daysActive = it
                xDaysCountDuration.text = it.toString()
                onChangeAction()
            }
        }

        layoutCycleActive.onClick {
            showChangeItemDialog(
                context.getString(R.string.days_active),
                1,
                999,
                reminderOptions.daysActive,
            ) {
                reminderOptions.daysActive = it
                cycleCountActive.text = it.toString()
                onChangeAction()
            }
        }

        layoutCycleInactive.onClick {
            showChangeItemDialog(
                context.getString(R.string.days_inactive),
                1,
                999,
                reminderOptions.daysInactive,
            ) {
                reminderOptions.daysInactive = it
                cycleCountInactive.text = it.toString()
                onChangeAction()
            }
        }

        layoutCycleToday.onClick {
            showChangeItemDialog(
                context.getString(R.string.today_is_cycle_day),
                1,
                reminderOptions.daysActive + reminderOptions.daysInactive,
                reminderOptions.todayCycle,
            ) {
                reminderOptions.todayCycle = it
                cycleCountToday.text = it.toString()
                onChangeAction()
            }
        }

    }

    private fun onCheckedChange(checkedId: Int) {
        onChangeAction()
        setOptions(
            when (checkedId) {
                R.id.radio_indefinitely -> {
                    ReminderOptions.indefinite()
                }
                R.id.radio_x_days -> {
                    ReminderOptions.finite(21)
                }
                R.id.radio_cycle -> {
                    ReminderOptions.cycling(21, 7, 1)
                }
                else -> ReminderOptions.empty()
            }, false
        )
    }

    private fun setContentVisibility(
        xDays: Boolean = false,
        cycle: Boolean = false
    ) {

        divider.isVisible = !(!xDays && !cycle)
        layoutXDays.isVisible = xDays
        layoutCycle.isVisible = cycle
    }

    private fun setAccentColor(accentColor: Int) {
        listOf(radioIndefinitely, radioXDays, radioCycle).forEach {
            it.buttonTintList = ColorStateList.valueOf(accentColor)
        }
    }

    private fun setIndefinite() {
        setContentVisibility()
    }

    private fun setFinite() {
        setContentVisibility(xDays = true, cycle = false)
        xDaysCountDuration.text = reminderOptions.daysActive.toString()
    }

    private fun setCycle() {
        setContentVisibility(xDays = false, cycle = true)
        cycleCountActive.text = reminderOptions.daysActive.toString()
        cycleCountInactive.text = reminderOptions.daysInactive.toString()
        cycleCountToday.text = reminderOptions.todayCycle.toString()
    }

    fun setOptions(options: ReminderOptions, toggleRadio: Boolean = true) {
        reminderOptions = options
        reminderOptions.lastReminderDate = null // Reset last date

        // Temporary remove check listener
        radioGroup.setOnCheckedChangeListener { _, _ -> }

        when {
            reminderOptions.isIndefinite() -> {
                setIndefinite()
                if (toggleRadio) radioGroup.check(R.id.radio_indefinitely)
            }
            reminderOptions.isFinite() -> {
                setFinite()
                if (toggleRadio) radioGroup.check(R.id.radio_x_days)
            }
            reminderOptions.isCycle() -> {
                setCycle()
                if (toggleRadio) radioGroup.check(R.id.radio_cycle)
            }
        }

        radioGroup.setOnCheckedChangeListener { _, checkedId ->
            onCheckedChange(checkedId)
        }

    }

    fun getOptions() = reminderOptions

    fun setButtonTint(color: Int) {
        setAccentColor(color)
    }

    @Suppress("SameParameterValue")
    private fun showChangeItemDialog(
        title: String,
        minValue: Int,
        maxValue: Int,
        value: Int,
        callback: (Int) -> Unit
    ) {

        var amount: Int = value
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_change_amount, this, false)

        val dialog = AlertDialog.Builder(context).apply {
            setView(view)
            setTitle(title)
            setPositiveButton(android.R.string.ok) { dialog, _ ->
                callback(amount)
                dialog.dismiss()
            }
            setNegativeButton(R.string.cancel) { dialog, _ ->
                dialog.dismiss()
            }
        }.create()
        val numberPickerAmount = view.findViewById<NumberPicker>(R.id.numberPickerAmount)

        numberPickerAmount.minValue = minValue
        numberPickerAmount.maxValue = maxValue
        numberPickerAmount.value = value
        numberPickerAmount.setOnValueChangedListener { _, _, newVal -> amount = newVal }

        dialog.show()
    }

    fun onChange(action: () -> Unit) {
        onChangeAction = action
    }
}