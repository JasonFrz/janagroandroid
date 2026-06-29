package com.example.janagroandroid.ui.history

import android.content.Context
import android.print.PrintAttributes
import android.print.PrintManager
import android.webkit.WebView
import android.webkit.WebViewClient
import com.example.janagroandroid.data.remote.dto.OrderDto
import java.text.NumberFormat
import java.util.Locale

object PrintHelper {

    fun printInvoice(context: Context, order: OrderDto) {
        val webView = WebView(context)
        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView, url: String) {
                createWebPrintJob(context, view, order)
            }
        }

        val htmlDocument = generateHtmlInvoice(order)
        webView.loadDataWithBaseURL(null, htmlDocument, "text/HTML", "UTF-8", null)
    }

    private fun createWebPrintJob(context: Context, webView: WebView, order: OrderDto) {
        val printManager = context.getSystemService(Context.PRINT_SERVICE) as PrintManager
        val jobName = "Invoice_Agrojan_${order.id}"

        val printAdapter = webView.createPrintDocumentAdapter(jobName)
        printManager.print(
            jobName,
            printAdapter,
            PrintAttributes.Builder().build()
        )
    }

    private fun generateHtmlInvoice(order: OrderDto): String {
        val formatCurrency = { amount: Double ->
            NumberFormat.getCurrencyInstance(Locale("id", "ID")).apply {
                maximumFractionDigits = 0
            }.format(amount).replace("Rp", "Rp ")
        }

        val buyerName = order.shippingAddress?.split("\n")?.firstOrNull() ?: "Pembeli"
        val storeName = order.merchant?.storeName ?: "Toko Tani"
        val orderDate = order.createdAt.split("T").firstOrNull() ?: "-"
        val paymentMethod = order.paymentMethod ?: "Transfer Bank"
        val courier = order.courier ?: "Reguler"

        var productsHtml = ""
        var productsSubtotal = 0.0

        order.items?.forEach { item ->
            val price = item.priceAtPurchaseDouble
            val subtotal = price * item.quantity
            productsSubtotal += subtotal

            productsHtml += """
                <tr>
                    <td>${item.product?.name ?: "Produk"}</td>
                    <td style="text-align: center;">${item.quantity}</td>
                    <td style="text-align: right;">${formatCurrency(price)}</td>
                    <td style="text-align: right;">${formatCurrency(subtotal)}</td>
                </tr>
            """.trimIndent()
        }

        val shippingCost = order.shippingCost?.toDoubleOrNull() ?: 0.0
        val voucherDiscount = order.voucherDiscount?.toDoubleOrNull() ?: 0.0
        val totalBeforeDiscount = productsSubtotal + shippingCost
        val grandTotal = order.totalPriceDouble + shippingCost - voucherDiscount

        return """
            <!DOCTYPE html>
            <html>
            <head>
                <style>
                    body { font-family: 'Helvetica Neue', Helvetica, Arial, sans-serif; padding: 20px; color: #333; }
                    .header { text-align: center; margin-bottom: 30px; }
                    .header h1 { color: #2E7D32; margin: 0; }
                    .info-section { display: flex; justify-content: space-between; margin-bottom: 20px; }
                    .info-box { width: 48%; }
                    .info-title { font-weight: bold; color: #777; font-size: 12px; margin-bottom: 5px; }
                    .info-content { font-size: 14px; margin-bottom: 10px; }
                    table { width: 100%; border-collapse: collapse; margin-bottom: 20px; }
                    th { background-color: #E8F5E9; padding: 10px; text-align: left; font-size: 14px; border-bottom: 2px solid #ddd; }
                    td { padding: 10px; border-bottom: 1px solid #eee; font-size: 14px; }
                    .summary { width: 50%; float: right; }
                    .summary-row { display: flex; justify-content: space-between; padding: 5px 0; font-size: 14px; }
                    .summary-row.total { font-weight: bold; font-size: 16px; border-top: 2px solid #ddd; padding-top: 10px; }
                    .total-value { color: #2E7D32; }
                    .discount-value { color: #D32F2F; }
                </style>
            </head>
            <body>
                <div class="header">
                    <h1>AGROJAN</h1>
                    <p>Faktur Pembelian</p>
                </div>
                
                <div class="info-section">
                    <div class="info-box">
                        <div class="info-title">INFORMASI PEMBELI</div>
                        <div class="info-content">Nama: $buyerName<br>Alamat: ${order.shippingAddress ?: "-"}</div>
                        
                        <div class="info-title">METODE PEMBAYARAN</div>
                        <div class="info-content">$paymentMethod</div>
                    </div>
                    <div class="info-box">
                        <div class="info-title">INFORMASI TOKO</div>
                        <div class="info-content">$storeName</div>
                        
                        <div class="info-title">TANGGAL PESANAN</div>
                        <div class="info-content">$orderDate</div>
                        
                        <div class="info-title">JASA KIRIM</div>
                        <div class="info-content">$courier</div>
                    </div>
                </div>

                <table>
                    <thead>
                        <tr>
                            <th>Produk</th>
                            <th style="text-align: center;">Qty</th>
                            <th style="text-align: right;">Harga</th>
                            <th style="text-align: right;">Subtotal</th>
                        </tr>
                    </thead>
                    <tbody>
                        $productsHtml
                    </tbody>
                </table>

                <div class="summary">
                    <div class="summary-row">
                        <span>Subtotal Produk</span>
                        <span>${formatCurrency(productsSubtotal)}</span>
                    </div>
                    <div class="summary-row">
                        <span>Ongkos Kirim</span>
                        <span>${formatCurrency(shippingCost)}</span>
                    </div>
                    <div class="summary-row">
                        <span>Total Sebelum Diskon</span>
                        <span>${formatCurrency(totalBeforeDiscount)}</span>
                    </div>
                    <div class="summary-row">
                        <span>Diskon Voucher</span>
                        <span class="discount-value">-${formatCurrency(voucherDiscount)}</span>
                    </div>
                    <div class="summary-row total">
                        <span>Total Pembayaran</span>
                        <span class="total-value">${formatCurrency(grandTotal)}</span>
                    </div>
                </div>
            </body>
            </html>
        """.trimIndent()
    }
}
