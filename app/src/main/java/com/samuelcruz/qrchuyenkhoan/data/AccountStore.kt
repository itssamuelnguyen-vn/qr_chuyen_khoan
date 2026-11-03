package com.samuelcruz.qrchuyenkhoan.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first

private val Context.accountData by preferencesDataStore(name = "account")

/** The account used last time, so the form opens already filled in. */
data class SavedAccount(val bin: String, val accountNumber: String, val accountName: String)

/** A tiny settings file (DataStore, like a one-row settings table) holding the last account. */
object AccountStore {
    private val BIN = stringPreferencesKey("bin")
    private val ACCOUNT_NUMBER = stringPreferencesKey("account_number")
    private val ACCOUNT_NAME = stringPreferencesKey("account_name")

    suspend fun load(context: Context): SavedAccount? {
        val prefs = context.accountData.data.first()
        val bin = prefs[BIN] ?: return null
        return SavedAccount(bin, prefs[ACCOUNT_NUMBER].orEmpty(), prefs[ACCOUNT_NAME].orEmpty())
    }

    suspend fun save(context: Context, account: SavedAccount) {
        context.accountData.edit {
            it[BIN] = account.bin
            it[ACCOUNT_NUMBER] = account.accountNumber
            it[ACCOUNT_NAME] = account.accountName
        }
    }
}
