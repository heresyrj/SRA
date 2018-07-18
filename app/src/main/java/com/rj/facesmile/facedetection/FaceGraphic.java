// Copyright 2018 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.rj.facesmile.facedetection;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;

import com.google.android.gms.vision.CameraSource;
import com.google.firebase.ml.vision.common.FirebaseVisionPoint;
import com.google.firebase.ml.vision.face.FirebaseVisionFace;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceLandmark;
import com.rj.facesmile.R;
import com.rj.facesmile.vision.GraphicOverlay;
import com.rj.facesmile.vision.GraphicOverlay.Graphic;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Graphic instance for rendering face position, orientation, and landmarks within an associated
 * graphic overlay view.
 */
public class FaceGraphic extends Graphic {
  private static final float FACE_POSITION_RADIUS = 10.0f;
  private static final float ID_TEXT_SIZE = 40.0f;
  private static final float ID_Y_OFFSET = 50.0f;
  private static final float ID_X_OFFSET = -50.0f;
  private static final float BOX_STROKE_WIDTH = 5.0f;

//  private static final int[] COLOR_CHOICES = {
//    Color.BLUE, Color.CYAN, Color.GREEN, Color.MAGENTA, Color.RED, Color.WHITE, Color.YELLOW
//  };
  private static final int[] COLOR_CHOICES = {
//        R.color.colorPrimary,
        0xFFFFE096
  };

  private static final int[] EMOJI_CHOICES = {
          0x1F369, 0x1f602, 0x1f60d, 0x1f436, 0x1f434, 0x1f437
  };
  private static int currentColorIndex = 0;
  private int randomEmoji = 0;

  private int facing;

  private final Paint facePositionPaint;
  private final Paint idPaint;
  private final Paint boxPaint;
  private final Paint emojiPaint;

  private volatile FirebaseVisionFace firebaseVisionFace;

  public FaceGraphic(GraphicOverlay overlay, int rand) {
    super(overlay);

    currentColorIndex = (currentColorIndex + 1) % COLOR_CHOICES.length;
    final int selectedColor = COLOR_CHOICES[currentColorIndex];
    randomEmoji = rand;

    facePositionPaint = new Paint();
    facePositionPaint.setColor(selectedColor);

    idPaint = new Paint();
    idPaint.setColor(selectedColor);
    idPaint.setTextSize(ID_TEXT_SIZE);

    boxPaint = new Paint();
    boxPaint.setColor(selectedColor);
    boxPaint.setStyle(Paint.Style.STROKE);
    boxPaint.setStrokeWidth(BOX_STROKE_WIDTH);

    emojiPaint = new Paint();
  }

  /**
   * Updates the face instance from the detection of the most recent frame. Invalidates the relevant
   * portions of the overlay to trigger a redraw.
   */
  public void updateFace(FirebaseVisionFace face, int facing) {
    firebaseVisionFace = face;
    this.facing = facing;
    postInvalidate();
  }

  /** Draws the face annotations for position on the supplied canvas. */
  @Override
  public void draw(Canvas canvas) {
    FirebaseVisionFace face = firebaseVisionFace;
    if (face == null) {
//      randomEmoji = ThreadLocalRandom.current().nextInt(0, EMOJI_CHOICES.length);
      return;
    }

    // Draws a circle at the position of the detected face, with the face's track id below.
    float x = translateX(face.getBoundingBox().centerX());
    float y = translateY(face.getBoundingBox().centerY());

    // Draws a bounding box around the face.
    float xOffset = scaleX(face.getBoundingBox().width() / 2.0f);
    float yOffset = scaleY(face.getBoundingBox().height() / 2.0f);
    emojiPaint.setTextSize(Math.min(face.getBoundingBox().width(), 400));
    float emojiOffset = Math.min(xOffset, scaleX(200));
    float left = x - xOffset;
    float top = y - yOffset;
    float right = x + xOffset;
    float bottom = y + yOffset;
    if (face.getSmilingProbability() <= .85) {
      canvas.drawRect(left, top, right, bottom, boxPaint);
//    canvas.drawText(
//            "happiness: " + String.format("%.2f", face.getSmilingProbability()),
//            left,
//            bottom + ID_Y_OFFSET,
//            idPaint);
    }
    if (face.getSmilingProbability() > .85) {
      String smileyEmoji = new String(Character.toChars(EMOJI_CHOICES[randomEmoji]));
      Log.d("Emoji", smileyEmoji);
      canvas.drawText(smileyEmoji, left + (right - left)/2.0f - emojiOffset, top + (bottom - top) /2.0f+emojiOffset, emojiPaint);
    }
  }

  private void drawLandmarkPosition(Canvas canvas, FirebaseVisionFace face, int landmarkID) {
    FirebaseVisionFaceLandmark landmark = face.getLandmark(landmarkID);
    if (landmark != null) {
      FirebaseVisionPoint point = landmark.getPosition();
      canvas.drawCircle(
              translateX(point.getX()),
              translateY(point.getY()),
              10f, idPaint);
    }
  }
}
