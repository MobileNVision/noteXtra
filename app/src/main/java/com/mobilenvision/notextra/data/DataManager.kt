package com.mobilenvision.notextra.data


import com.mobilenvision.notextra.data.local.db.DbHelper
import com.mobilenvision.notextra.data.local.prefs.PreferencesHelper
import java.io.Serializable

interface DataManager : DbHelper, PreferencesHelper, Serializable {

}