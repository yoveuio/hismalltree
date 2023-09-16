package com.muxu.utils.entity

/**
 * Catalog info
 */
data class CatalogTreeDTO (
    val type: String? = null,
    val name: String? = null,
    val id: Long? = null,
    var subCatalog: List<CatalogTreeDTO>? = null)
