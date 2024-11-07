package com.kloc.unistore.navigation

import com.kloc.unistore.util.Constants.CART
import com.kloc.unistore.util.Constants.CATEGORY_DETAILS
import com.kloc.unistore.util.Constants.CATEGORY_ID
import com.kloc.unistore.util.Constants.ORDER_DETAILS_SCREEN
import com.kloc.unistore.util.Constants.PRODUCT
import com.kloc.unistore.util.Constants.PRODUCT_DETAILS
import com.kloc.unistore.util.Constants.PRODUCT_ID
import com.kloc.unistore.util.Constants.SCHOOL_DETAILS
import com.kloc.unistore.util.Constants.SCHOOL_ID
import com.kloc.unistore.util.Constants.STUDENT_DETAILS_SCREEN


sealed class Screen(val route: String) {
    object SchoolDetailsScreen: Screen(SCHOOL_DETAILS)
    object SchoolCategoryScreen : Screen("${CATEGORY_DETAILS}/{${SCHOOL_ID}}") {
        fun createRoute(schoolId: Int) = "${CATEGORY_DETAILS}/$schoolId"
    }
    object ProductDetailsScreen : Screen("${PRODUCT_DETAILS}/{${CATEGORY_ID}}") {
        fun createRoute(categoryId: Int) = "${PRODUCT_DETAILS}/$categoryId"
    }

    object ProductScreen : Screen("${PRODUCT}/{${PRODUCT_ID}}") {
        fun createRoute(productId: Int) = "${PRODUCT}/$productId"
    }
    object CartScreen : Screen(CART)
    object OrderDetailsScreen : Screen(ORDER_DETAILS_SCREEN)
    object StudentDetailsScreen : Screen(STUDENT_DETAILS_SCREEN)

}