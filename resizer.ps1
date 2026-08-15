Add-Type -AssemblyName System.Drawing

$srcPath = "C:\Users\premk\.gemini\antigravity-ide\brain\e50402db-4abc-4bcc-91f9-6d0c675e31d6\threewin_logo_v2_1786796441695.jpg"
$baseDest = "app\src\main\res"

if (-Not (Test-Path $srcPath)) {
    Write-Host "Source file not found!"
    exit 1
}

$srcImage = [System.Drawing.Image]::FromFile($srcPath)

$sizes = @{
    "mdpi" = 48
    "hdpi" = 72
    "xhdpi" = 96
    "xxhdpi" = 144
    "xxxhdpi" = 192
}

foreach ($density in $sizes.Keys) {
    $size = $sizes[$density]
    $destDir = Join-Path $baseDest "mipmap-$density"
    
    if (-Not (Test-Path $destDir)) {
        New-Item -ItemType Directory -Path $destDir | Out-Null
    }

    # Standard icons
    $destBmp = New-Object System.Drawing.Bitmap($size, $size)
    $g = [System.Drawing.Graphics]::FromImage($destBmp)
    $g.InterpolationMode = [System.Drawing.Drawing2D.InterpolationMode]::HighQualityBicubic
    $g.DrawImage($srcImage, 0, 0, $size, $size)
    $g.Dispose()

    $destPath = Join-Path $destDir "ic_launcher.png"
    $destBmp.Save($destPath, [System.Drawing.Imaging.ImageFormat]::Png)
    
    $destPathRound = Join-Path $destDir "ic_launcher_round.png"
    $destBmp.Save($destPathRound, [System.Drawing.Imaging.ImageFormat]::Png)

    $destBmp.Dispose()
    
    # Adaptive foreground (108x108 base, scaled for each density: mdpi=108, hdpi=162, xhdpi=216, xxhdpi=324, xxxhdpi=432)
    # The actual foreground should be 108dp. 
    # mdpi: 108x108
    $fgSize = [int]($size * (108.0 / 48.0))
    $fgBmp = New-Object System.Drawing.Bitmap($fgSize, $fgSize)
    $gFg = [System.Drawing.Graphics]::FromImage($fgBmp)
    $gFg.InterpolationMode = [System.Drawing.Drawing2D.InterpolationMode]::HighQualityBicubic
    $gFg.Clear([System.Drawing.Color]::White)
    
    # We want the icon to fit in the safe zone (72dp -> size*1.5), but the logo has its own padding.
    # Let's just scale the original image down a bit to fit safely in the middle.
    # We will make the image occupy 60dp out of 108dp.
    $imgSize = [int]($size * (72.0 / 48.0))
    $offset = ($fgSize - $imgSize) / 2
    
    $gFg.DrawImage($srcImage, $offset, $offset, $imgSize, $imgSize)
    $gFg.Dispose()
    
    $fgPath = Join-Path $destDir "ic_launcher_foreground.png"
    $fgBmp.Save($fgPath, [System.Drawing.Imaging.ImageFormat]::Png)
    $fgBmp.Dispose()

    Write-Host "Processed $density"
}

$srcImage.Dispose()
Write-Host "Done converting to proper PNGs."
