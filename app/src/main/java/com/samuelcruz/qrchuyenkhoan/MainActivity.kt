package com.samuelcruz.qrchuyenkhoan

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import com.samuelcruz.qrchuyenkhoan.ui.FormScreen
import com.samuelcruz.qrchuyenkhoan.ui.MainViewModel
import com.samuelcruz.qrchuyenkhoan.ui.QrTheme
import com.samuelcruz.qrchuyenkhoan.ui.ResultScreen

/** Two screens, no navigation library: the form, and the card once one has been made. */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            QrTheme {
                val viewModel: MainViewModel = viewModel()
                val result = viewModel.result
                if (result == null) {
                    FormScreen(viewModel)
                } else {
                    BackHandler(onBack = viewModel::closeResult)
                    ResultScreen(result, onBack = viewModel::closeResult)
                }
            }
        }
    }
}
