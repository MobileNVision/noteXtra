package com.mobilenvision.notextra.data.local.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.mobilenvision.notextra.data.model.db.Category


@Dao
interface CategoryDao {
    @Delete
    fun deleteCategory(category: Category)

    @Query("DELETE FROM categories WHERE name = :categoryName")
    fun deleteCategoryByName(categoryName: String)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertCategory(category: Category)

    @Query("SELECT * FROM categories")
    fun loadAllCategories(): List<Category>

    @Query("SELECT * FROM categories WHERE id = :categoryId")
    fun loadCategoryById(categoryId: String): Category

    @Query("UPDATE categories SET name = :newCategoryName WHERE name = :exCategoryName")
    fun updateCategory(newCategoryName: String, exCategoryName: String)
}
