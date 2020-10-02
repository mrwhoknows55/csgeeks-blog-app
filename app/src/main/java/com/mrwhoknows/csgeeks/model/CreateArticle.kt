package com.mrwhoknows.csgeeks.model

import android.os.Parcel
import android.os.Parcelable

data class CreateArticle(
    var author: String?,
    val content: String?,
    var description: String?,
    var tags: String?,
    var thumbnail: String?,
    var title: String?
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(author)
        parcel.writeString(content)
        parcel.writeString(description)
        parcel.writeString(tags)
        parcel.writeString(thumbnail)
        parcel.writeString(title)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<CreateArticle> {
        override fun createFromParcel(parcel: Parcel): CreateArticle {
            return CreateArticle(parcel)
        }

        override fun newArray(size: Int): Array<CreateArticle?> {
            return arrayOfNulls(size)
        }
    }
}