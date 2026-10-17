package com.example.foodchecker.data.local.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

private const val HISTORY_SELECT = """
    SELECT p.barcode AS barcode,
           p.name AS name,
           p.brand AS brand,
           p.ingredients AS ingredients,
           MAX(s.scanned_at) AS scanned_at,
           MAX(s.id) AS last_scan_id,
           COUNT(s.id) AS scan_count
    FROM products AS p
    INNER JOIN scans AS s ON s.barcode = p.barcode
"""

@Dao
abstract class HistoryDao {

    @Query("$HISTORY_SELECT GROUP BY p.barcode ORDER BY last_scan_id DESC")
    abstract fun observeHistory(): Flow<List<HistoryRow>>

    @Query("$HISTORY_SELECT WHERE p.barcode = :barcode GROUP BY p.barcode")
    abstract fun observeHistoryItem(barcode: String): Flow<HistoryRow?>

    @Query("$HISTORY_SELECT WHERE p.barcode = :barcode GROUP BY p.barcode")
    abstract suspend fun getHistoryItem(barcode: String): HistoryRow?

    @Upsert
    abstract suspend fun upsertProduct(product: ProductEntity)

    @Insert
    abstract suspend fun insertScan(scan: ScanEntity): Long

    @Transaction
    open suspend fun recordScan(product: ProductEntity, scannedAt: LocalDate) {
        upsertProduct(product)
        insertScan(ScanEntity(barcode = product.barcode, scannedAt = scannedAt))
    }
}
