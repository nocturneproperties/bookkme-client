//
//  FlowLayout.swift
//  iosApp
//
//  Created by BookkMe on 24.11.2026.
//  Copyright © 2026 BookkMe. All rights reserved.
//

import SwiftUI

struct FlowLayout: Layout {

	var spacing: CGFloat = 6

	func sizeThatFits(proposal: ProposedViewSize, subviews: Subviews, cache: inout ()) -> CGSize {
		let rows = arrange(subviews: subviews, maxWidth: proposal.width ?? .infinity)
		let width = rows.map(\.width).max() ?? 0
		let height = rows.map(\.height).reduce(0, +) + spacing * CGFloat(max(rows.count - 1, 0))
		return CGSize(width: width, height: height)
	}

	func placeSubviews(in bounds: CGRect, proposal: ProposedViewSize, subviews: Subviews, cache: inout ()) {
		var y = bounds.minY
		for row in arrange(subviews: subviews, maxWidth: bounds.width) {
			var x = bounds.minX
			for index in row.indices {
				let size = subviews[index].sizeThatFits(.unspecified)
				subviews[index].place(at: CGPoint(x: x, y: y), proposal: ProposedViewSize(size))
				x += size.width + spacing
			}
			y += row.height + spacing
		}
	}

	private func arrange(subviews: Subviews, maxWidth: CGFloat) -> [FlowRow] {
		var rows: [FlowRow] = []
		var current = FlowRow()
		for index in subviews.indices {
			let size = subviews[index].sizeThatFits(.unspecified)
			let nextWidth = current.indices.isEmpty ? size.width : current.width + spacing + size.width
			if nextWidth > maxWidth && !current.indices.isEmpty {
				rows.append(current)
				current = FlowRow()
			}
			current.add(index: index, size: size, spacing: spacing)
		}
		if !current.indices.isEmpty {
			rows.append(current)
		}
		return rows
	}
}

private struct FlowRow {
	var indices: [Int] = []
	var width: CGFloat = 0
	var height: CGFloat = 0

	mutating func add(index: Int, size: CGSize, spacing: CGFloat) {
		width += indices.isEmpty ? size.width : spacing + size.width
		height = max(height, size.height)
		indices.append(index)
	}
}
