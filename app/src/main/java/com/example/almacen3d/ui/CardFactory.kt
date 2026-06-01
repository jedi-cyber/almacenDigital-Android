package com.example.almacen3d.ui

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.RippleDrawable
import android.content.res.ColorStateList
import android.graphics.Typeface
import android.text.SpannableString
import android.text.Spanned
import android.text.style.StyleSpan
import android.view.Gravity
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.example.almacen3d.R

class CardFactory(private val context: Context) {

    /**
     * Compact Google-style suggestion row. One tap opens the product route.
     * Primary line shows the product name (with the matched query bolded),
     * secondary line shows SKU + location.
     */
    fun createSuggestionRow(
        primary: String,
        secondary: String,
        query: String,
        onClick: () -> Unit
    ): LinearLayout {
        val rippleColor = ColorStateList.valueOf(Color.parseColor("#1F1A73E8"))
        val baseShape = GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            cornerRadius = 12.dp().toFloat()
            setColor(Color.parseColor("#FFFFFF"))
        }
        val rippleBg = RippleDrawable(rippleColor, baseShape, null)

        val row = LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            background = rippleBg
            isClickable = true
            isFocusable = true
            setPadding(14.dp(), 10.dp(), 14.dp(), 10.dp())
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply { topMargin = 6.dp() }
            setOnClickListener { onClick() }
        }

        val icon = ImageView(context).apply {
            setImageResource(android.R.drawable.ic_menu_search)
            imageTintList = ColorStateList.valueOf(Color.parseColor("#5F6368"))
            layoutParams = LinearLayout.LayoutParams(18.dp(), 18.dp()).apply {
                marginEnd = 12.dp()
            }
        }
        row.addView(icon)

        val textColumn = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(
                0,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                1f
            )
        }

        textColumn.addView(TextView(context).apply {
            text = boldMatch(primary, query)
            setTextColor(Color.parseColor("#1F2937"))
            textSize = 14f
            maxLines = 1
            ellipsize = android.text.TextUtils.TruncateAt.END
        })

        textColumn.addView(TextView(context).apply {
            text = secondary
            setTextColor(Color.parseColor("#5F6368"))
            textSize = 12f
            maxLines = 1
            ellipsize = android.text.TextUtils.TruncateAt.END
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply { topMargin = 2.dp() }
        })

        row.addView(textColumn)
        return row
    }

    private fun boldMatch(text: String, query: String): CharSequence {
        val q = query.trim()
        if (q.isEmpty()) return text
        val idx = text.lowercase().indexOf(q.lowercase())
        if (idx == -1) return text
        return SpannableString(text).apply {
            setSpan(StyleSpan(Typeface.BOLD), idx, idx + q.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
    }

    fun createCard(
        title: String,
        body: String,
        primaryActionText: String? = null,
        primaryAction: (() -> Unit)? = null,
        secondaryActionText: String? = null,
        secondaryAction: (() -> Unit)? = null
    ): LinearLayout {
        val card = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(12.dp(), 12.dp(), 12.dp(), 12.dp())
            background = ContextCompat.getDrawable(context, R.drawable.product_card_background)
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply { topMargin = 12.dp() }
        }

        val topRow = LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.TOP
        }

        // Image Placeholder
        topRow.addView(ImageView(context).apply {
            layoutParams = LinearLayout.LayoutParams(60.dp(), 60.dp())
            scaleType = ImageView.ScaleType.CENTER_INSIDE
            background = ContextCompat.getDrawable(context, R.drawable.image_placeholder)
            setImageResource(android.R.drawable.ic_dialog_map)
            alpha = 0.5f
        })

        // Text Content
        val contentLayout = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f).apply {
                marginStart = 12.dp()
            }
        }

        contentLayout.addView(TextView(context).apply {
            text = title
            setTextColor(ContextCompat.getColor(context, R.color.text_primary))
            textSize = 14f
            setTypeface(typeface, Typeface.BOLD)
            maxLines = 2
        })

        val bodyLines = body.split("\n")
        bodyLines.forEach { line ->
            contentLayout.addView(TextView(context).apply {
                text = line
                setTextColor(ContextCompat.getColor(context, R.color.text_secondary))
                textSize = 11f
                layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                ).apply { topMargin = 2.dp() }
            })
        }

        topRow.addView(contentLayout)

        // Actions on the Right
        val actionLayout = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.END
            layoutParams = LinearLayout.LayoutParams(110.dp(), ViewGroup.LayoutParams.WRAP_CONTENT).apply {
                marginStart = 8.dp()
            }
        }

        if (primaryActionText != null) {
            actionLayout.addView(Button(context).apply {
                text = "Ver ruta 3D"
                setTextColor(ContextCompat.getColor(context, R.color.white))
                background = ContextCompat.getDrawable(context, R.drawable.blue_button_background)
                isAllCaps = false
                textSize = 10f
                setTypeface(typeface, Typeface.BOLD)
                setPadding(0, 0, 0, 0)
                setCompoundDrawablesWithIntrinsicBounds(android.R.drawable.ic_menu_directions, 0, 0, 0)
                layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    34.dp()
                )
                setOnClickListener { primaryAction?.invoke() }
            })
        }

        if (secondaryActionText != null) {
            actionLayout.addView(Button(context).apply {
                text = "Editar"
                setTextColor(ContextCompat.getColor(context, R.color.warehouse_text))
                background = ContextCompat.getDrawable(context, R.drawable.white_button_background)
                isAllCaps = false
                textSize = 10f
                setTypeface(typeface, Typeface.BOLD)
                setPadding(0, 0, 0, 0)
                setCompoundDrawablesWithIntrinsicBounds(android.R.drawable.ic_menu_edit, 0, 0, 0)
                layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    34.dp()
                ).apply { topMargin = 6.dp() }
                setOnClickListener { secondaryAction?.invoke() }
            })
        }

        topRow.addView(actionLayout)
        card.addView(topRow)

        // Bottom Info (Estante, Sección, Nivel)
        val infoRow = LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply { topMargin = 8.dp() }
            gravity = Gravity.CENTER_VERTICAL
        }

        val locationText = bodyLines.lastOrNull { it.contains("·") } ?: ""
        if (locationText.isNotBlank()) {
            infoRow.addView(TextView(context).apply {
                text = locationText
                setTextColor(ContextCompat.getColor(context, R.color.text_secondary))
                textSize = 11f
                setCompoundDrawablesWithIntrinsicBounds(android.R.drawable.ic_dialog_map, 0, 0, 0)
                compoundDrawablePadding = 4.dp()
                val tint = ContextCompat.getColor(context, R.color.text_secondary)
                compoundDrawables[0]?.setTint(tint)
            })
        }

        card.addView(infoRow)

        return card
    }
}
