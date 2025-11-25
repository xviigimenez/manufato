package com.example.manufato

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "Manufato.db"
        private const val DATABASE_VERSION = 1
        
        private const val TABLE_PRODUCTS = "products"
        private const val COLUMN_ID = "id"
        private const val COLUMN_NAME = "name"
        private const val COLUMN_DESCRIPTION = "description"
        private const val COLUMN_PRICE = "price"
        private const val COLUMN_QUANTITY = "quantity"
        private const val COLUMN_CATEGORY = "category"
        private const val COLUMN_IS_AVAILABLE = "is_available"
        private const val COLUMN_SALES = "sales"
        private const val COLUMN_IMAGE_URI = "image_uri"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTable = ("CREATE TABLE " + TABLE_PRODUCTS + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_NAME + " TEXT,"
                + COLUMN_DESCRIPTION + " TEXT,"
                + COLUMN_PRICE + " REAL,"
                + COLUMN_QUANTITY + " INTEGER,"
                + COLUMN_CATEGORY + " TEXT,"
                + COLUMN_IS_AVAILABLE + " INTEGER,"
                + COLUMN_SALES + " INTEGER,"
                + COLUMN_IMAGE_URI + " TEXT" + ")")
        db.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PRODUCTS)
        onCreate(db)
    }

    fun addProduct(product: Product): Long {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(COLUMN_NAME, product.name)
        values.put(COLUMN_DESCRIPTION, product.description)
        values.put(COLUMN_PRICE, product.price)
        values.put(COLUMN_QUANTITY, product.quantity)
        values.put(COLUMN_CATEGORY, product.category)
        values.put(COLUMN_IS_AVAILABLE, if (product.isAvailable) 1 else 0)
        values.put(COLUMN_SALES, product.sales)
        values.put(COLUMN_IMAGE_URI, product.imageUri)

        val id = db.insert(TABLE_PRODUCTS, null, values)
        db.close()
        return id
    }

    fun updateProduct(product: Product): Int {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(COLUMN_NAME, product.name)
        values.put(COLUMN_DESCRIPTION, product.description)
        values.put(COLUMN_PRICE, product.price)
        values.put(COLUMN_QUANTITY, product.quantity)
        values.put(COLUMN_CATEGORY, product.category)
        values.put(COLUMN_IS_AVAILABLE, if (product.isAvailable) 1 else 0)
        values.put(COLUMN_SALES, product.sales)
        values.put(COLUMN_IMAGE_URI, product.imageUri)

        val rows = db.update(TABLE_PRODUCTS, values, "$COLUMN_ID = ?", arrayOf(product.id.toString()))
        db.close()
        return rows
    }

    fun deleteProduct(id: Long) {
        val db = this.writableDatabase
        db.delete(TABLE_PRODUCTS, "$COLUMN_ID = ?", arrayOf(id.toString()))
        db.close()
    }

    fun getAllProducts(): List<Product> {
        val productList = ArrayList<Product>()
        val selectQuery = "SELECT * FROM $TABLE_PRODUCTS"
        val db = this.readableDatabase
        val cursor = db.rawQuery(selectQuery, null)

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ID))
                val name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME))
                val description = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION))
                val price = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_PRICE))
                val quantity = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_QUANTITY))
                val category = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CATEGORY))
                val isAvailable = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_IS_AVAILABLE)) == 1
                val sales = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_SALES))
                val imageUri = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_IMAGE_URI))

                val product = Product(
                    id = id,
                    name = name,
                    description = description,
                    price = price,
                    quantity = quantity,
                    category = category,
                    isAvailable = isAvailable,
                    sales = sales,
                    imageUri = imageUri
                )
                productList.add(product)
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return productList
    }

    fun getProduct(id: Long): Product? {
        val db = this.readableDatabase
        val cursor = db.query(
            TABLE_PRODUCTS,
            arrayOf(COLUMN_ID, COLUMN_NAME, COLUMN_DESCRIPTION, COLUMN_PRICE, COLUMN_QUANTITY, COLUMN_CATEGORY, COLUMN_IS_AVAILABLE, COLUMN_SALES, COLUMN_IMAGE_URI),
            "$COLUMN_ID = ?",
            arrayOf(id.toString()),
            null,
            null,
            null,
            null
        )

        var product: Product? = null
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                val name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME))
                val description = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION))
                val price = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_PRICE))
                val quantity = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_QUANTITY))
                val category = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CATEGORY))
                val isAvailable = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_IS_AVAILABLE)) == 1
                val sales = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_SALES))
                val imageUri = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_IMAGE_URI))

                product = Product(
                    id = id,
                    name = name,
                    description = description,
                    price = price,
                    quantity = quantity,
                    category = category,
                    isAvailable = isAvailable,
                    sales = sales,
                    imageUri = imageUri
                )
            }
            cursor.close()
        }
        db.close()
        return product
    }
}
