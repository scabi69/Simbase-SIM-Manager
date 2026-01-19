package com.xabi.simbase.api

data class SimStateRequest(
    val state: String
)

data class SimStateResponse(
    val message: String?,
    val state: String?
)


data class SimCard(
    val name: String?,
    val state: String?,
    val tags: List<String>?,
    val plan_id: String?,
    val coverage: String?,
    val iccid: String,
    val sim_profile: String?,
    val msisdn: String?,
    val hardware: String?,
    val imei: String?,
    val imei_lock: String?,
    val public_ip: String?,
    val private_network_ip: String?,
    val current_month_usage: CurrentMonthUsage?,
    val current_month_costs: CurrentMonthCosts?,
    val autodisable: String?
)

data class CurrentMonthUsage(
    val total_sessions: Int?,
    val data_sessions: Int?,
    val zero_sessions: Int?,
    val data: Int?,
    val sms_mo: Int?,
    val sms_mt: Int?,
    val voice: Int?,
    val line_rental: Int?
)

data class CurrentMonthCosts(
    val total: String?,
    val data: String?,
    val sms: String?,
    val sms_mo: String?,
    val sms_mt: String?,
    val voice: String?,
    val other: String?,
    val line_rental: String?
)

data class SimListResponse(
    val cursor: String?,
    val simcards: List<SimCard>?,
    val has_more: Boolean,
    val count: Int
)
