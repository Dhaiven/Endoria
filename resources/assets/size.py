import argparse
from PIL import Image

def slice_and_resize(input_path, n, widthLeft, widthRight, height, output_path):
    # Open the source image
    img = Image.open(input_path)
    src_width, src_height = img.size

    # Compute slice width
    slice_width = src_width // n
    if slice_width * n != src_width:
        raise ValueError(f"Image width {src_width} is not evenly divisible by n={n}")

    # Slice, zoom (crop), resize, and collect
    resized_slices = []
    for i in range(n):
        left = i * slice_width
        right = left + slice_width
        box = (left, 0, right, src_height)
        slice_img = img.crop(box)

        # Zoom by cropping x pixels on both sides and y pixels from top and bottom
        zoom_box = (
            widthLeft,
            height,
            slice_img.width - widthRight,
            slice_img.height - height
        )
        zoomed = slice_img.crop(zoom_box)
        resized_slices.append(zoomed)

    # Create a new image to combine all resized slices side by side
    combined = Image.new('RGBA', (src_width - (widthLeft + widthRight) * n, src_height - height * 2))
    for i, rs in enumerate(resized_slices):
        combined.paste(rs, (max(0, i * (slice_width - (widthLeft + widthRight))), 0))

    # Save the final image
    combined.save(output_path)
    print(f"Saved combined image: {output_path}")

if __name__ == '__main__':
    parser = argparse.ArgumentParser(
        description='Slice a PNG into n vertical segments, zoom into center, resize each, and recombine.')
    parser.add_argument('input', help='Path to input PNG image')
    parser.add_argument('n', type=int, help='Number of vertical slices')
    parser.add_argument('widthLeft', type=int, help='Target widthLeft of each slice')
    parser.add_argument('widthRight', type=int, help='Target widthRight of each slice')
    parser.add_argument('height', type=int, help='Target height of each slice')
    parser.add_argument('output', help='Path to output combined PNG image')
    args = parser.parse_args()

    slice_and_resize(args.input, args.n, args.widthLeft, args.widthRight, args.height, args.output)
