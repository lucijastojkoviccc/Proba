package elfak.mosis.petfinder.model

import androidx.lifecycle.ViewModel
import elfak.mosis.petfinder.data.NewPost

class NewPostViewModel: ViewModel() {
    var NewPosts: ArrayList<NewPost> = ArrayList<NewPost>()
    var FilteredNewPosts: ArrayList<NewPost> = ArrayList<NewPost>()
    var NewPostsDatum: ArrayList<NewPost> = ArrayList<NewPost>()
    var npType:ArrayList<NewPost> = ArrayList<NewPost>()
    var IDNewPosts: String = ""
    var selected: NewPost?=null


    var postComments: ArrayList<String> = ArrayList<String>()
    fun addComment(comm:String)
    {
        postComments.add(comm)
    }
    fun getAllComments():ArrayList<String>
    {
        return postComments
    }


    public fun addPet(pet: NewPost) {
        NewPosts.add(pet)
    }
    fun getNewPosts(): List<NewPost> {
        return NewPosts
    }
    fun addID(ID: String) {
        IDNewPosts = ID
    }
    fun returnID(): String {
        return IDNewPosts
    }
    fun setToNull() {
        NewPosts.clear()
    }
    fun addFilteredPost(NewPost: NewPost) {
        FilteredNewPosts.add(NewPost)
    }

    fun getFilteredPosts(): ArrayList<NewPost> {
        return FilteredNewPosts
    }
    fun addNewPostsDatum(NewPost: NewPost) {
        NewPostsDatum.add(NewPost)
    }

    fun vratiNewPostsDatum(): ArrayList<NewPost> {
        return NewPostsDatum
    }
    fun addNPtype(newPost: NewPost)
    {
        npType.add(newPost);
    }

    fun getNPtype ():ArrayList<NewPost>
    {
        return npType;
    }
}