package land.sungbin.androidprojecttemplate.data.model.auth

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class User(
    @SerializedName("username")
    val username: String = "doro"
) : Parcelable
