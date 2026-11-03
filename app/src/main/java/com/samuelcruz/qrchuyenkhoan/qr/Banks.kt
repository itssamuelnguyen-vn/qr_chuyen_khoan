package com.samuelcruz.qrchuyenkhoan.qr

/**
 * A bank (or e-wallet) on the NAPAS network. [bin] is the 6-digit acquirer id that goes into the QR code; [code] is
 * the short code VietQR uses for it (VCB, TCB, ...).
 */
data class Bank(val bin: String, val code: String, val shortName: String, val name: String)

/**
 * All NAPAS members, most used first. Copied from https://api.vietqr.io/v2/banks on 2026-11-03 so the app works
 * offline; refresh this list by hand when a bank merges or a new one appears.
 */
object Banks {
    val all: List<Bank> = listOf(
        Bank("970415", "ICB", "VietinBank", "Ngân hàng TMCP Công thương Việt Nam"),
        Bank("970436", "VCB", "Vietcombank", "Ngân hàng TMCP Ngoại Thương Việt Nam"),
        Bank("970418", "BIDV", "BIDV", "Ngân hàng TMCP Đầu tư và Phát triển Việt Nam"),
        Bank("970405", "VBA", "Agribank", "Ngân hàng Nông nghiệp và Phát triển Nông thôn Việt Nam"),
        Bank("970448", "OCB", "OCB", "Ngân hàng TMCP Phương Đông"),
        Bank("970422", "MB", "MBBank", "Ngân hàng TMCP Quân đội"),
        Bank("970407", "TCB", "Techcombank", "Ngân hàng TMCP Kỹ thương Việt Nam"),
        Bank("970416", "ACB", "ACB", "Ngân hàng TMCP Á Châu"),
        Bank("970432", "VPB", "VPBank", "Ngân hàng TMCP Việt Nam Thịnh Vượng"),
        Bank("970423", "TPB", "TPBank", "Ngân hàng TMCP Tiên Phong"),
        Bank("970403", "STB", "Sacombank", "Ngân hàng TMCP Sài Gòn Tài Lộc"),
        Bank("970437", "HDB", "HDBank", "Ngân hàng TMCP Phát triển Thành phố Hồ Chí Minh"),
        Bank("970454", "VCCB", "VietCapitalBank", "Ngân hàng TMCP Bản Việt"),
        Bank("970429", "SCB", "SCB", "Ngân hàng TMCP Sài Gòn"),
        Bank("970441", "VIB", "VIB", "Ngân hàng TMCP Quốc tế Việt Nam"),
        Bank("970443", "SHB", "SHB", "Ngân hàng TMCP Sài Gòn - Hà Nội"),
        Bank("970431", "EIB", "Eximbank", "Ngân hàng TMCP Xuất Nhập khẩu Việt Nam"),
        Bank("970426", "MSB", "MSB", "Ngân hàng TMCP Hàng Hải"),
        Bank("546034", "CAKE", "CAKE", "TMCP Việt Nam Thịnh Vượng - Ngân hàng số CAKE by VPBank"),
        Bank("546035", "Ubank", "Ubank", "TMCP Việt Nam Thịnh Vượng - Ngân hàng số Ubank by VPBank"),
        Bank("963388", "TIMO", "Timo", "Ngân hàng số Timo by Ban Viet Bank (Timo by Ban Viet Bank)"),
        Bank("971005", "VTLMONEY", "ViettelMoney", "Tổng Công ty Dịch vụ số Viettel - Chi nhánh tập đoàn công nghiệp viễn thông Quân Đội"),
        Bank("971011", "VNPTMONEY", "VNPTMoney", "Trung tâm dịch vụ tài chính số VNPT- Chi nhánh Tổng công ty truyền thông (VNPT Fintech)"),
        Bank("970400", "SGICB", "SaigonBank", "Ngân hàng TMCP Sài Gòn Công Thương"),
        Bank("970409", "BAB", "BacABank", "Ngân hàng TMCP Bắc Á"),
        Bank("971133", "PVDB", "PVcomBank Pay", "Ngân hàng số PVcomBank"),
        Bank("971025", "momo", "MoMo", "CTCP Dịch Vụ Di Động Trực Tuyến"),
        Bank("970412", "PVCB", "PVcomBank", "Ngân hàng TMCP Đại Chúng Việt Nam"),
        Bank("970414", "MBV", "MBV", "Ngân hàng TNHH MTV Việt Nam Hiện Đại"),
        Bank("970419", "NCB", "NCB", "Ngân hàng TMCP Quốc Dân"),
        Bank("970424", "SHBVN", "ShinhanBank", "Ngân hàng TNHH MTV Shinhan Việt Nam"),
        Bank("970425", "ABB", "ABBANK", "Ngân hàng TMCP An Bình"),
        Bank("970427", "VAB", "VietABank", "Ngân hàng TMCP Việt Á"),
        Bank("970428", "NAB", "NamABank", "Ngân hàng TMCP Nam Á"),
        Bank("970430", "PGB", "PGBank", "Ngân hàng TMCP Thịnh vượng và Phát triển"),
        Bank("970433", "VIETBANK", "VietBank", "Ngân hàng TMCP Việt Nam Thương Tín"),
        Bank("970438", "BVB", "BaoVietBank", "Ngân hàng TMCP Bảo Việt"),
        Bank("970440", "SEAB", "SeABank", "Ngân hàng TMCP Đông Nam Á"),
        Bank("970446", "COOPBANK", "COOPBANK", "Ngân hàng Hợp tác xã Việt Nam"),
        Bank("970449", "LPB", "LPBank", "Ngân hàng TMCP Lộc Phát Việt Nam"),
        Bank("970452", "KLB", "KienLongBank", "Ngân hàng TMCP Kiên Long"),
        Bank("668888", "KBank", "KBank", "Ngân hàng Đại chúng TNHH Kasikornbank"),
        Bank("970466", "KEBHANAHCM", "KEBHanaHCM", "Ngân hàng KEB Hana – Chi nhánh Thành phố Hồ Chí Minh"),
        Bank("970467", "KEBHANAHN", "KEBHANAHN", "Ngân hàng KEB Hana – Chi nhánh Hà Nội"),
        Bank("977777", "MAFC", "MAFC", "Công ty Tài chính TNHH MTV Mirae Asset (Việt Nam)"),
        Bank("999888", "VBSP", "VBSP", "Ngân hàng Chính sách Xã hội"),
        Bank("963668", "BNP HN", "BNP HN", "Ngân hàng BNP Paribas – Chi nhánh Hà Nội"),
        Bank("555666", "BIDC", "BIDC", "Ngân hàng Đầu Tư Và Phát Triển Campuchia - Chi nhánh Hà Nội"),
        Bank("963666", "BNP HCM", "BNP HCM", "Ngân hàng BNP Paribas – Chi nhánh Thành phố Hồ Chí Minh"),
        Bank("971101", "ZALOPAY", "ZaloPay", "Ví điện tử ZaloPay"),
        Bank("963688", "BOCHK", "BOCHK", "Ngân hàng Bank of China (Hong Kong) Limited – Chi nhánh Hồ Chí Minh"),
        Bank("970465", "BSP", "BSP", "Ngân hàng SinoPac - Chi nhánh Thành phố Hồ Chí Minh"),
        Bank("168999", "CUBHCM", "CUBHCM", "Ngân hàng Cathay United Bank – Chi nhánh Hồ Chí Minh"),
        Bank("963686", "ESUN", "ESUN", "Ngân hàng Thương mại TNHH E.SUN – Chi nhánh Đồng Nai (ESUNBANK)"),
        Bank("963669", "ICBC", "ICBC", "Ngân hàng Công thương Trung Quốc – Chi nhánh thành phố Hà Nội"),
        Bank("971032", "MVAS", "MVAS", "Trung tâm Dịch vụ số Mobifone – Chi nhánh Tổng Công ty viễn thông Mobifone"),
        Bank("963326", "TNEX", "TNEX", "TNEX by MSB"),
        Bank("458761", "HSBC", "HSBC", "Ngân hàng TNHH MTV HSBC (Việt Nam)"),
        Bank("970442", "HLBVN", "HongLeong", "Ngân hàng TNHH MTV Hong Leong Việt Nam"),
        Bank("970455", "IBK - HN", "IBKHN", "Ngân hàng Công nghiệp Hàn Quốc - Chi nhánh Hà Nội"),
        Bank("970456", "IBK - HCM", "IBKHCM", "Ngân hàng Công nghiệp Hàn Quốc - Chi nhánh TP. Hồ Chí Minh"),
        Bank("970434", "IVB", "IndovinaBank", "Ngân hàng TNHH Indovina"),
        Bank("970406", "Vikki", "Vikki", "Ngân hàng TNHH Một thành viên số Vikki"),
        Bank("801011", "NHB HN", "Nonghyup", "Ngân hàng Nonghyup - Chi nhánh Hà Nội"),
        Bank("796500", "DBS", "DBSBank", "DBS Bank Ltd - Chi nhánh Thành phố Hồ Chí Minh"),
        Bank("970439", "PBVN", "PublicBank", "Ngân hàng TNHH MTV Public Việt Nam"),
        Bank("970410", "SCVN", "StandardChartered", "Ngân hàng TNHH MTV Standard Chartered Bank Việt Nam"),
        Bank("533948", "CITIBANK", "Citibank", "Ngân hàng Citibank, N.A. - Chi nhánh Hà Nội"),
        Bank("422589", "CIMB", "CIMB", "Ngân hàng TNHH MTV CIMB Việt Nam"),
        Bank("970458", "UOB", "UnitedOverseas", "Ngân hàng United Overseas - Chi nhánh TP. Hồ Chí Minh"),
        Bank("970444", "CBB", "CBBank", "Ngân hàng Thương mại trách nhiệm hữu hạn một thành viên Ngoại thương Công nghệ số (Vietcombank Neo Limited)"),
        Bank("970408", "GPB", "GPBank", "Ngân hàng Thương mại TNHH MTV Dầu Khí Toàn Cầu"),
        Bank("970421", "VRB", "VRB", "Ngân hàng Liên doanh Việt - Nga"),
        Bank("970457", "WVN", "Woori", "Ngân hàng TNHH MTV Woori Việt Nam"),
        Bank("970462", "KBHN", "KookminHN", "Ngân hàng Kookmin - Chi nhánh Hà Nội"),
        Bank("970463", "KBHCM", "KookminHCM", "Ngân hàng Kookmin - Chi nhánh Thành phố Hồ Chí Minh"),
    )

    fun byBin(bin: String): Bank? = all.firstOrNull { it.bin == bin }
}
