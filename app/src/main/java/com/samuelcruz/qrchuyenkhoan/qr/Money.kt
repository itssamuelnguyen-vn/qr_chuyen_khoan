package com.samuelcruz.qrchuyenkhoan.qr

/** Groups digits by thousands with dots, the Vietnamese way: 1500000 → "1.500.000". */
fun groupThousands(amount: Long): String =
    amount.toString().reversed().chunked(3).joinToString(".").reversed()

/** "150.000 đ" */
fun formatVnd(amount: Long): String = groupThousands(amount) + " đ"
