package io.github.kn65op.domag.ui.dialogs

import android.util.Log
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import io.github.kn65op.android.lib.type.FixedPointNumber
import io.github.kn65op.domag.data.database.database.AppDatabase
import io.github.kn65op.domag.data.database.operations.NotEnoughAmountToConsume
import io.github.kn65op.domag.data.database.operations.consumeItem
import io.github.kn65op.domag.data.database.entities.Category

class ConsumeDialogController(
    private val activity: FragmentActivity,
    private val db: AppDatabase
) {

    fun startConsumeDialog(
        itemId: Int,
        fullName: String,
        category: Category,
        currentAmount: FixedPointNumber,
    ) {
        val dialog = ConsumeItemDialog(
            fullName,
            category.unit,
            currentAmount,
            object : ConsumeItemDialog.ConsumeItemDialogListener {
                override suspend fun onConsume(amount: FixedPointNumber) {
                    consume(itemId, amount, fullName, category, currentAmount)
                }
            })
        dialog.show(activity.supportFragmentManager, "ConsumeDialog")
    }

    private suspend fun consume(
        itemId: Int,
        amount: FixedPointNumber,
        fullName: String,
        category: Category,
        currentAmount: FixedPointNumber
    ) {
        Log.i(LOG_TAG, "Will consume")
        try {
            db.consumeItem(itemId, amount)
            Log.i(LOG_TAG, "Consumed $fullName: $amount")
        } catch (notEnough: NotEnoughAmountToConsume) {
            retry(notEnough, itemId, fullName, category, currentAmount)
        }
    }

    private fun retry(
        notEnough: NotEnoughAmountToConsume,
        itemId: Int,
        fullName: String,
        category: Category,
        currentAmount: FixedPointNumber
    ) {
        Log.w(
            LOG_TAG,
            "Too much requested for consume: ${notEnough.requestedAmount}, while only ${notEnough.amount} available, trying again"
        )
        notifyToUser()
        startConsumeDialog(itemId, fullName, category, currentAmount)
    }

    private fun notifyToUser() {
        val context = activity.applicationContext
        activity.runOnUiThread {
            val toast = Toast.makeText(
                context,
                "Too much requested to consume: $",
                Toast.LENGTH_SHORT
            )
            toast.show()
        }
    }

    companion object {
        private const val LOG_TAG = "ConsumeDialogController"
    }
}