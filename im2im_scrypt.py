import torch
import os
import torchvision
from torch.utils.mobile_optimizer import optimize_for_mobile

model = torch.load("Im2Im.pth")
model.eval()
example = torch.rand(1, 3, 224, 224)

torchscript_model = torch.jit.script(model)
torchscript_model_optimized = optimize_for_mobile(torchscript_model)

torchscript_model_optimized._save_for_lite_interpreter("model_Im2Im_lite.pt")