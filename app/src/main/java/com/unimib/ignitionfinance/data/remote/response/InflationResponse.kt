package com.unimib.ignitionfinance.data.remote.response

import com.google.gson.annotations.SerializedName

data class InflationResponse(
    @SerializedName("header") val header: InflationHeader,
    @SerializedName("dataSets") val dataSets: List<InflationDataSet>,
    @SerializedName("structure") val structure: InflationStructure
)

data class InflationHeader(
    @SerializedName("id") val id: String,
    @SerializedName("test") val test: Boolean,
    @SerializedName("prepared") val prepared: String,
    @SerializedName("sender") val sender: InflationSender
)

data class InflationSender(
    @SerializedName("id") val id: String
)

data class InflationDataSet(
    @SerializedName("action") val action: String,
    @SerializedName("validFrom") val validFrom: String,
    @SerializedName("series") val series: Map<String, InflationSeries>
)

data class InflationSeries(
    @SerializedName("attributes") val attributes: List<Any?>,
    @SerializedName("observations") val observations: Map<String, List<Any?>>
)

data class InflationStructure(
    @SerializedName("links") val links: List<InflationLink>,
    @SerializedName("name") val name: String,
    @SerializedName("dimensions") val dimensions: InflationDimensions,
    @SerializedName("attributes") val attributes: InflationAttributes
)

data class InflationLink(
    @SerializedName("title") val title: String,
    @SerializedName("rel") val rel: String,
    @SerializedName("href") val href: String
)

data class InflationDimensions(
    @SerializedName("series") val series: List<InflationDimension>,
    @SerializedName("observation") val observation: List<InflationObservation>
)

data class InflationDimension(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("values") val values: List<InflationValue>
)

data class InflationObservation(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("role") val role: String?,
    @SerializedName("values") val values: List<InflationTimeValue>
)

data class InflationValue(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String
)

data class InflationTimeValue(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("start") val start: String,
    @SerializedName("end") val end: String
)

data class InflationAttributes(
    @SerializedName("series") val series: List<InflationAttribute>,
    @SerializedName("observation") val observation: List<InflationAttribute>
)

data class InflationAttribute(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("values") val values: List<InflationAttributeValue>
)

data class InflationAttributeValue(
    @SerializedName("id") val id: String?,
    @SerializedName("name") val name: String
)
