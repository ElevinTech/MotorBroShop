package com.elevintech.motorbroshop.AdsView

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.elevintech.motorbroshop.Constants
import com.elevintech.motorbroshop.R
import kotlinx.android.synthetic.main.activity_ads_view.*

class AdsViewActivity : AppCompatActivity() {

    val samPaint_par = "Samurai is the first brand of spray specifically designed for the motorcycle market. Samurai paints are made in Malaysia and are gasoline resistant and flexible or crack resistant. Samurai is the best spray paint in the Philippine market today. For more information, you can check out our facebook page or join our Facebook group through the following links."
    val owens_par = "Owens is the go-to brand for high quality motorcycle front and rear shock absorbers. Over the years, Owens has refined its quality of shock absorbers. Owens is also the brand with the most models of shock absorbers available at reasonable prices. To know more about Owens, please visit our Facebook page: "
    val tsr_par = "The TSR brand has been in the Philippine market for over 30 years. TSR provides high quality and the most complete models and sizes available for sprockets, chains and chain and sprocket sets at very affordable prices. TSR sprockets are made from S45C steel alloy which is what the Japanese brands use. TSR also uses black zinc as a coating for the sprockets to protect it against rusting and improves the overall quality of the sprokets. For more info, please vist the following: "
    val osaki_par = "The Osaki brand has been known for its quality for over 30 years in the Philippine market. Osaki is the pioneer of the chain and sprocket set. Other products also include chain, cables, alloy rims, spokes, hubs and various gold bolts and nuts. You can expect complete models and sizes from Osaki. "
    val epower_par = "E-power is the first brand to be dedicated in providing high quality motorcycle electrical parts. E-power products include CDI, rectifiers/regulators, LED bulbs, horns, headlights, taillights, winker lamps, and much more electrical products. "
    val comstar_par = "Comstar products are a line of professional motorcycle parts. This means that all Comstar products exceed the quality specified by the Japanese manufacturers. Comstar products are stronger and more durable as compared with others. Most also would have special features. Products include series 7000 aluminum alloy rims, ceramic brake pads and shoes, magwheels, stainless oil filters, high-flow air filters and paper based clutch linings. "
    val cyclefix_par = "CycleFix provides a complete line of specialized motorcycle tools and machines. These are ideal for do-it-yourself repairs to professional mechanics and even motorcycle parts and repair stores. CycleFix also has a store in 388 Quezon Avenue, Quezon City for your motorycle repair needs. "
    val tiger_par = "Tiger brand of gaskets are 100% made in Thailand. They are made by the factory which makes gaskets for the Japanese manufacturers. All gaskets are made up to original manufacturer specifcations. They are oil and heat resistant as well. Since all the gaskets are precise in size, there is no need to put gasket glue when installing Tiger gaskets. "
    val okk_par = "Tiger brand of gaskets are 100% made in Thailand. They are made by the factory which makes gaskets for the Japanese manufacturers. All gaskets are made up to original manufacturer specifcations. They are oil and heat resistant as well. Since all the gaskets are precise in size, there is no need to put gasket glue when installing Tiger gaskets. "
    val motmot_par = "Motmot products are made specially with the Filipino riders in mind. Motmot is a proudly Pinoy brand. Initial products brake shoes, bulbs, handle levers, etc. Please stay tuned for more information. "
    val posh_par = "Posh Motorcycle Shop is the largest wholesale and retail motorcycle parts and service center in Metro Manila. Posh carries the most models of parts and famous brands such as Samurai, Osaki, Comstar, OKK, Tiger, Owens, TSR, etc. Posh Motorcycle Shop is also available on Lazada and Shopee. "
    val gpc_par = "The GPC brand has been in the Philippine market for over 20 years. GPC stands for Genuine Parts Company. This means that all GPC parts follow the OEM specifications of the original parts manufacturers. GPC products includes brakes, clutches, CVT parts, engine parts, etc."

    val gpcHeader = "GPC"
    val samPaintHeader = "SAMURAI PAINT"
    val owensHeader = "OWENS"
    val tsrHeader = "TSR"
    val osakiHeader = "OSAKI"
    val epowerHeader = "E-POWER"
    val comstarHeader = "COMSTAR"
    val cyclefixHeader = "CYCLEFIX"
    val tigerHeader = "TIGER"
    val okkHeader = "OKK"
    val motmotHeader = "MOTMOT"
    val poshHeader = "POSH MOTORCYCLE SHOP"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ads_view)

        backView.setOnClickListener {
            finish()
        }

        var result = intent.getStringExtra("adType")

        when(result){

            Constants.AD_TYPE.E_POWER -> {
                adsHeader.text = epowerHeader
                adsBody.text = epower_par
                hyperLink1.text = "https://www.facebook.com/epower.ph"
                adsImageView.setImageResource(R.drawable.ads_epower)
            }

            Constants.AD_TYPE.SAMURAI_PAINT -> {
                adsHeader.text = samPaintHeader
                adsBody.text = samPaint_par
                hyperLink1.text = "https://www.facebook.com/SamuraiPhilippines/"
                hyperLink2.text = "https://www.facebook.com/groups/SamuraiPaintPhilippines/"
                adsImageView.setImageResource(R.drawable.ads_samurai)
            }

            Constants.AD_TYPE.OWENS -> {
                adsHeader.text = owensHeader
                adsBody.text = owens_par
                hyperLink1.text = "https://www.facebook.com/OwensShockAbsorber/"
                adsImageView.setImageResource(R.drawable.ads_owens)
            }

            Constants.AD_TYPE.TSR -> {
                adsHeader.text = tsrHeader
                adsBody.text = tsr_par
                hyperLink1.text = "https://www.facebook.com/TSRSprockets/"
                adsImageView.setImageResource(R.drawable.ads_tsr)
            }

            Constants.AD_TYPE.OSAKI -> {
                adsHeader.text = osakiHeader
                adsBody.text = osaki_par
                hyperLink1.text = "https://www.facebook.com/OSAKIph/"
                adsImageView.setImageResource(R.drawable.ads_osaki)
            }

            Constants.AD_TYPE.COMSTAR -> {
                adsHeader.text = comstarHeader
                adsBody.text = comstar_par
                hyperLink1.text = "https://www.facebook.com/ComstarPH/"
                adsImageView.setImageResource(R.drawable.ads_comstar)
            }

            Constants.AD_TYPE.CYCLEFIX -> {
                adsHeader.text = cyclefixHeader
                adsBody.text = cyclefix_par
                hyperLink1.text = "https://www.facebook.com/CycleFixPH"
                adsImageView.setImageResource(R.drawable.ads_cyclefix)
            }

            Constants.AD_TYPE.TIGER -> {
                adsHeader.text = tigerHeader
                adsBody.text = tiger_par
                hyperLink1.text = "https://www.facebook.com/TigerGaskets/"
                adsImageView.setImageResource(R.drawable.ads_tiger)
            }

            Constants.AD_TYPE.OKK -> {
                adsHeader.text = okkHeader
                adsBody.text = okk_par
                hyperLink1.text = "https://www.facebook.com/OKKinc/"
                adsImageView.setImageResource(R.drawable.ads_okk)
            }

            Constants.AD_TYPE.MOTMOT -> {
                adsHeader.text = motmotHeader
                adsBody.text = motmot_par
                adsImageView.setImageResource(R.drawable.ads_motmot)
            }

            Constants.AD_TYPE.POSH -> {
                adsHeader.text = poshHeader
                adsBody.text = posh_par
                hyperLink1.text = "https://www.facebook.com/PoshMotorcycle/"
                hyperLink2.text = "https://www.lazada.com.ph/shop/posh-motorcycle-shop/?spm=a2o4l.pdp.seller.1.1c183e24weB6jt&itemId=5449224&channelSource=pdp"
                adsImageView.setImageResource(R.drawable.ads_posh)
            }

            Constants.AD_TYPE.GPC -> {
                adsHeader.text = gpcHeader
                adsBody.text = gpc_par
                hyperLink1.text = "https://www.facebook.com/GPC.Ph/"
                adsImageView.setImageResource(R.drawable.ads_gpc)
            }
        }




    }
}
