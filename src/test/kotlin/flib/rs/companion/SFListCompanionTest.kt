package flib.rs.companion

import io.kotest.core.spec.style.ExpectSpec
import io.kotest.matchers.shouldBe
import org.xrpn.flib.rs.SFList
import org.xrpn.flib.internal.tool.flistReverseBuilder

class SFListCompanionTest : ExpectSpec({

    val fl10 = flistReverseBuilder(0, 10)
    context("build empty") {
        val autS = SFList.of<String>()
        val autI = SFList.of<Int>()
        expect("an empty list") {
            autS.empty shouldBe true
            autS shouldBe /*unfortunately */ autI
        }
    }

    context("build not empty") {
        val aut1 = SFList.of(fl10)
        val aut2 = SFList.of(fl10)
        expect("a non empty list") {
            aut1.ne shouldBe true
            aut1.fix() shouldBe fl10
            (aut1.fix() === fl10) shouldBe true
            aut2.fix() shouldBe fl10
            (aut2.fix() === fl10) shouldBe true
            aut1 shouldBe aut2
            (aut1 === aut2) shouldBe false
        }
    }
})